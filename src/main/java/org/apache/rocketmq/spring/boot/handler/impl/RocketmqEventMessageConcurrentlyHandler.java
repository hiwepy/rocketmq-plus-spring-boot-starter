package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.AbstractRouteableMessageHandler;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry-point handler for concurrent message consumption that wraps each
 * received message in a {@link RocketmqEvent} and dispatches it through the
 * configured {@link HandlerChain}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class RocketmqEventMessageConcurrentlyHandler extends AbstractRouteableMessageHandler<RocketmqEvent> implements MessageConcurrentlyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqEventMessageConcurrentlyHandler.class);

	/**
	 * @param filterChainResolver the chain resolver used to route events
	 */
	public RocketmqEventMessageConcurrentlyHandler(HandlerChainResolver<RocketmqEvent> filterChainResolver) {
		super(filterChainResolver);
	}

	@Override
    /**
     * <p>Pre handle.</p>
     * @param msgExt
     * @param context
     * @return the pre handle
     */
	public boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		return true;
	}

	@Override
    /**
     * <p>Handle message.</p>
     * @param msgExt
     * @param context
     */
	public void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		// Build the original (root) chain.
		HandlerChain<RocketmqEvent>	originalChain = new ProxiedHandlerChain();
		// Execute the event handler chain.
		this.doHandler(new RocketmqEvent(msgExt, context.getMessageQueue()), originalChain);
	}

	@Override
    /**
     * <p>Post handle.</p>
     * @param msgExt
     * @param context
     */
	public void postHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		
	}

	@Override
    /**
     * <p>After completion.</p>
     * @param msgExt
     * @param context
     * @param ex
     */
	public void afterCompletion(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception ex) throws Exception {
		if(ex != null) {
			LOG.warn("Consume message failed. messageExt:{}", msgExt, ex);
		}
	}
	
}