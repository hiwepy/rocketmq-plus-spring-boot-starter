package org.apache.rocketmq.spring.boot.disruptor;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;

import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * Disruptor {@link EventTranslatorOneArg} that copies a received
 * {@link MessageExt} into a {@link RocketmqDisruptorEvent} for concurrent
 * processing, retaining the {@link ConsumeConcurrentlyContext}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class RocketmqDataConcurrentlyEventTranslator implements EventTranslatorOneArg<RocketmqDisruptorEvent, MessageExt> {

	/** The concurrent consume context associated with the message. */
	private ConsumeConcurrentlyContext context;

	/**
	 * @param context the concurrent consume context
	 * @throws Exception never thrown by this implementation
	 */
	public RocketmqDataConcurrentlyEventTranslator(ConsumeConcurrentlyContext context) throws Exception {
		this.context = context;
	}

	@Override
	public void translateTo(RocketmqDisruptorEvent event, long sequence, MessageExt msgExt) {

		event.setMessageExt(msgExt);
		event.setTopic(msgExt.getTopic());
		event.setTag(msgExt.getTags());
		event.setBody(msgExt.getBody());

	}

	/** @return the concurrent consume context */
	public ConsumeConcurrentlyContext getContext() {
		return context;
	}

	/** @param context the concurrent consume context */
	public void setContext(ConsumeConcurrentlyContext context) {
		this.context = context;
	}

}