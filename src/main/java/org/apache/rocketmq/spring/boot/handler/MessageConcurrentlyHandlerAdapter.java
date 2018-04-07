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
package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;

public abstract class MessageConcurrentlyHandlerAdapter implements MessageConcurrentlyHandler {

	/**
	 * This implementation always returns {@code true}.
	 */
	@Override
	public boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		return true;
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		
	}
	
	/**
	 * This implementation is empty.
	 */
	@Override
	public void postHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		
	}
	
	/**
	 * This implementation is empty.
	 */
	@Override
	public void afterCompletion(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception ex) throws Exception {
		
	}

}
