package org.apache.rocketmq.spring.boot.disruptor;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;

import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * Disruptor {@link EventTranslatorOneArg} that copies a received
 * {@link MessageExt} into a {@link RocketmqDisruptorEvent} for orderly
 * processing, retaining the {@link ConsumeOrderlyContext}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class RocketmqDataOrderlyEventTranslator implements EventTranslatorOneArg<RocketmqDisruptorEvent, MessageExt> {

	/** The orderly consume context associated with the message. */
	private ConsumeOrderlyContext context;

	/**
	 * @param context the orderly consume context
	 * @throws Exception never thrown by this implementation
	 */
	public RocketmqDataOrderlyEventTranslator(ConsumeOrderlyContext context) throws Exception {
		this.context = context;
	}

	@Override
    /**
     * <p>Translate to.</p>
     * @param event
     * @param sequence
     * @param msgExt
     */
	public void translateTo(RocketmqDisruptorEvent event, long sequence, MessageExt msgExt) {

		event.setMessageExt(msgExt);
		event.setTopic(msgExt.getTopic());
		event.setTag(msgExt.getTags());
		event.setBody(msgExt.getBody());

	}

	/** @return the orderly consume context */
	public ConsumeOrderlyContext getContext() {
		return context;
	}

	/** @param context the orderly consume context */
	public void setContext(ConsumeOrderlyContext context) {
		this.context = context;
	}

}