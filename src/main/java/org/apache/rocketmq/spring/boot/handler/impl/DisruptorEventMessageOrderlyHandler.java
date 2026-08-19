package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataOrderlyEventTranslator;
import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;
import org.apache.rocketmq.spring.boot.handler.MessageOrderlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.dsl.Disruptor;

/**
 * Orderly message handler that publishes received messages onto a LMAX
 * Disruptor ring buffer for asynchronous processing.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DisruptorEventMessageOrderlyHandler implements MessageOrderlyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DisruptorEventMessageOrderlyHandler.class);

	/** The Disruptor ring buffer used to publish events. */
	private Disruptor<RocketmqDisruptorEvent> disruptor;

	@Override
    /**
     * <p>Pre handle.</p>
     * @param msgExt
     * @param context
     * @return the pre handle
     */
	public boolean preHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		return true;
	}

	@Override
    /**
     * <p>Handle message.</p>
     * @param msgExt
     * @param context
     */
	public void handleMessage(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		// Publish the message onto the Disruptor ring buffer.
		disruptor.publishEvent(new RocketmqDataOrderlyEventTranslator(context), msgExt);
	}
	
	@Override
    /**
     * <p>Post handle.</p>
     * @param msgExt
     * @param context
     */
	public void postHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		
	}

	@Override
    /**
     * <p>After completion.</p>
     * @param msgExt
     * @param context
     * @param ex
     */
	public void afterCompletion(MessageExt msgExt, ConsumeOrderlyContext context, Exception ex) throws Exception {
		if(ex != null) {
			LOG.warn("Consume message failed. messageExt:{}", msgExt, ex);
		}
	}

}