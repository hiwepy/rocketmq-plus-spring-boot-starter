package org.apache.rocketmq.spring.boot.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataEventTranslator;
import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.lmax.disruptor.dsl.Disruptor;

public class MessageDisruptorProcessor implements MessageProcessor, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(MessageDisruptorProcessor.class);
	
	@Autowired
	private Disruptor<RocketmqDataEvent> disruptor;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
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