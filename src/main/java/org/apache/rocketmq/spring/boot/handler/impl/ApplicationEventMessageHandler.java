package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.MessageHandler;

@Component
public class ApplicationEventMessageHandler implements MessageHandler, ApplicationEventPublisherAware {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationEventMessageHandler.class);
	private ApplicationEventPublisher eventPublisher;
	
	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			// 发布消息到达的事件，以便分发到每个tag的监听方法
			getEventPublisher().publishEvent(new RocketmqEvent(msgExt));
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}

}