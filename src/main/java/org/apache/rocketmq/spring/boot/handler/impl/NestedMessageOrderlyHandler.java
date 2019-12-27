/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.handler.MessageOrderlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 嵌套的顺序消息处理器：解决统一消息交由多个处理实现处理问题
 */
public class NestedMessageOrderlyHandler implements MessageOrderlyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(NestedMessageOrderlyHandler.class);
	private final List<MessageOrderlyHandler> handlers;

	public NestedMessageOrderlyHandler(List<MessageOrderlyHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public boolean preHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		return true;
	}
	
	@Override
	public void handleMessage(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		if(isNested()){
			for (MessageOrderlyHandler handler : getHandlers()) {
				handler.handleMessage(msgExt, context);
			}
		} else {
			 throw new IllegalArgumentException(" Not Found MessageOrderlyHandler .");
		}
	}
	
	@Override
	public void postHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
	}

	@Override
	public void afterCompletion(MessageExt msgExt, ConsumeOrderlyContext context, Exception ex) throws Exception {
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
	
	public List<MessageOrderlyHandler> getHandlers() {
		return handlers;
	}

}
