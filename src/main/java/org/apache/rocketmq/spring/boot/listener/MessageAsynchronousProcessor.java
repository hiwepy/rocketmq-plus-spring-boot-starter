package org.apache.rocketmq.spring.boot.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.RocketmqConfiguration;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class MessageAsynchronousProcessor implements MessageProcessor, ApplicationEventPublisherAware {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqConfiguration.class);
	private ApplicationEventPublisher eventPublisher;
	
	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			// 发布消息到达的事件，以便分发到每个tag的监听方法
			getEventPublisher().publishEvent(new RocketmqEvent(msgExt, context));
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