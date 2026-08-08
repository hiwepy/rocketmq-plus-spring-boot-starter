package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataConcurrentlyEventTranslator;
import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.dsl.Disruptor;

/**
 * Concurrent message handler that publishes received messages onto a LMAX
 * Disruptor ring buffer for asynchronous processing.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class DisruptorEventMessageConcurrentlyHandler implements MessageConcurrentlyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DisruptorEventMessageConcurrentlyHandler.class);

	/** The Disruptor ring buffer used to publish events. */
	private Disruptor<RocketmqDisruptorEvent> disruptor;

	@Override
	public boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		return true;
	}

	@Override
	public void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		// Publish the message onto the Disruptor ring buffer.
		disruptor.publishEvent(new RocketmqDataConcurrentlyEventTranslator(context), msgExt);
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
	

}