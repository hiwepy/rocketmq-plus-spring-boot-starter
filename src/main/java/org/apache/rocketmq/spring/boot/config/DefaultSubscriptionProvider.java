/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package org.apache.rocketmq.spring.boot.config;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.rocketmq.spring.boot.annotation.RocketmqPushConsumer;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;

@SuppressWarnings("rawtypes")
public class DefaultSubscriptionProvider implements SubscriptionProvider, ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSubscriptionProvider.class);
	private ApplicationContext applicationContext;
	public final String SELECTOR_EXPRESSS_EPARATOR = " || ";
	
	@Override
	public Map<String, String> subscription() {
		
		Map<String /* topic */, String /* selectorExpress */> subscription = new HashMap<String, String>();
		
		// 查找Spring上下文中注册的EventHandler接口实现
		Map<String, EventHandler> beansOfType = getApplicationContext().getBeansOfType(EventHandler.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, EventHandler>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, EventHandler> entry = ite.next();
				if (entry.getValue() instanceof RocketmqEventMessageConcurrentlyHandler ||
						entry.getValue() instanceof RocketmqEventMessageOrderlyHandler) {
					//跳过入口实现类
					continue;
				}
				RocketmqPushConsumer annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), RocketmqPushConsumer.class);
				if(annotationType == null) {
					// 注解为空，则打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", RocketmqPushConsumer.class, entry.getValue().getClass(), entry.getKey());
				} else {
					//handlerChainDefinitionMap.put(annotationType.value(), entry.getKey());
					if("*".equals(annotationType.tags())) {
						subscription.put(annotationType.topic(), annotationType.tags());
					} else {
						//拆分
						String[] tagArr = StringUtils.tokenizeToStringArray(annotationType.tags());
						// 调用消费端，订阅消息
						String selectorExpress = StringUtils.join(tagArr, SELECTOR_EXPRESSS_EPARATOR);
						subscription.put(annotationType.topic(), selectorExpress );
					}
				}
			}
		}
		
		return subscription;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	

}
