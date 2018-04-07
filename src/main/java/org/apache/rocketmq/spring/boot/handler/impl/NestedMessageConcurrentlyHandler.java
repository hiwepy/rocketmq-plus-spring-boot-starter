/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
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
package org.apache.rocketmq.spring.boot.handler.impl;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @className	： NestedMessageConcurrentlyHandler
 * @description	： 嵌套的多路消息处理器：解决统一消息交由多个处理实现处理问题
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年11月13日 上午10:36:12
 * @version 	V1.0
 */
public class NestedMessageConcurrentlyHandler implements MessageConcurrentlyHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(NestedMessageConcurrentlyHandler.class);
	private final List<MessageConcurrentlyHandler> handlers;

	public NestedMessageConcurrentlyHandler(List<MessageConcurrentlyHandler> handlers) {
		this.handlers = handlers;
	}

	
	@Override
	public boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		return true;
	}
	
	@Override
	public void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		if(isNested()){
			for (MessageConcurrentlyHandler handler : getHandlers()) {
				handler.handleMessage(msgExt, context);
			}
		} else {
			 throw new IllegalArgumentException(" Not Found MessageConcurrentlyHandler .");
		}
	}

	@Override
	public void postHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
	}

	@Override
	public void afterCompletion(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception ex) throws Exception {
		if(ex != null) {
			LOG.warn("Consume message failed. messageExt:{}", msgExt, ex);
		}
	}
	
	protected boolean isNested() {
		if(CollectionUtils.isEmpty(getHandlers())){
			return false;
		}
		return true;
	}
	
	public List<MessageConcurrentlyHandler> getHandlers() {
		return handlers;
	}

}
