/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.rocketmq.spring.boot;

import org.apache.rocketmq.spring.boot.enums.ConsumeMode;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.def.PathMatchingHandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class RocketmqPushEventTemplate {

	@Autowired
	private RocketmqEventMessageOrderlyHandler messageOrderlyHandler;
	@Autowired
	private RocketmqEventMessageConcurrentlyHandler messageConcurrentlyHandler;
	@Autowired
	private RocketmqPushConsumerProperties pushConsumerProperties;
	
	protected PathMatchingHandlerChainResolver getChainResolver() {
		PathMatchingHandlerChainResolver chainResolver = null;
		if( pushConsumerProperties != null && pushConsumerProperties.isEnabled() ) {
			//根据不同的消费模式创建对应的handler
			if (ConsumeMode.ORDERLY.compareTo(pushConsumerProperties.getConsumeMode()) == 0) {
				chainResolver = (PathMatchingHandlerChainResolver) getMessageOrderlyHandler().getHandlerChainResolver();
			}else {
				chainResolver = (PathMatchingHandlerChainResolver) getMessageConcurrentlyHandler().getHandlerChainResolver();
			}
		}
		return chainResolver;
	}
	
	public void unsubscribe(String topic, String tags, String handlerName) {
		PathMatchingHandlerChainResolver chainResolver = getChainResolver();
		if(chainResolver == null) {
			return;
		}
		HandlerChainManager<RocketmqEvent> chainManager = chainResolver.getHandlerChainManager();
		
		chainManager.getHandlers().remove(handlerName);
		
		//拆分
		String[] tagArr = StringUtils.tokenizeToStringArray(tags, ",");
		for (String tag : tagArr) {
			// topic/tags/keys
			String rule = new StringBuilder().append(topic).append("/").append(tag).append("/*").toString();
			chainManager.getHandlerChains().remove(rule);
		}
		
	}
	
	public void subscribe(String topic, String tags, String handlerName, EventHandler<RocketmqEvent> handler) {

		PathMatchingHandlerChainResolver chainResolver = getChainResolver();
		if(chainResolver == null) {
			return;
		}
		HandlerChainManager<RocketmqEvent> chainManager = chainResolver.getHandlerChainManager();

		//构造一个独一无二的handler名称
		String chainDefinition = handlerName;
		//创建一个新的Handler实例
		chainManager.addHandler(chainDefinition, handler);
		
		//拆分
		String[] tagArr = StringUtils.tokenizeToStringArray(tags, ",");
		for (String tag : tagArr) {
			// 构造一个消息分发规则对应的handler责任链
			// topic/tags/keys
			String rule = new StringBuilder().append(topic).append("/").append(tag).append("/*").toString();
			chainManager.createChain(rule, chainDefinition);
		}
		
	}

	public RocketmqEventMessageOrderlyHandler getMessageOrderlyHandler() {
		return messageOrderlyHandler;
	}

	public void setMessageOrderlyHandler(RocketmqEventMessageOrderlyHandler messageOrderlyHandler) {
		this.messageOrderlyHandler = messageOrderlyHandler;
	}

	public RocketmqEventMessageConcurrentlyHandler getMessageConcurrentlyHandler() {
		return messageConcurrentlyHandler;
	}

	public void setMessageConcurrentlyHandler(RocketmqEventMessageConcurrentlyHandler messageConcurrentlyHandler) {
		this.messageConcurrentlyHandler = messageConcurrentlyHandler;
	}
}
