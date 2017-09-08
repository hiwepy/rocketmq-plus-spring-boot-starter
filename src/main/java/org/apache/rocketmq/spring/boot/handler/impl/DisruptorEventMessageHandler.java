package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataEventTranslator;
import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;
import org.apache.rocketmq.spring.boot.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorEventMessageHandler implements MessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DisruptorEventMessageHandler.class);
	
	private Disruptor<RocketmqDisruptorEvent> disruptor;
		
	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			// 生产消息
			disruptor.publishEvent(new RocketmqDataEventTranslator(context), msgExt);
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}

}