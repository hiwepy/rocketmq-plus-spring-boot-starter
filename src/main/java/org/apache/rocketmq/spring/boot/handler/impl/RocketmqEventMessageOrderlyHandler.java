package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.AbstractRouteableMessageHandler;
import org.apache.rocketmq.spring.boot.handler.MessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry-point handler for orderly message consumption that wraps each received
 * message in a {@link RocketmqEvent} and dispatches it through the configured
 * {@link HandlerChain}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class RocketmqEventMessageOrderlyHandler extends AbstractRouteableMessageHandler<RocketmqEvent> implements MessageOrderlyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqEventMessageOrderlyHandler.class);

	/**
	 * @param filterChainResolver the chain resolver used to route events
	 */
	public RocketmqEventMessageOrderlyHandler(HandlerChainResolver<RocketmqEvent> filterChainResolver) {
		super(filterChainResolver);
	}

	@Override
	public boolean preHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		return true;
	}

	@Override
	public void handleMessage(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		// Build the original (root) chain.
		HandlerChain<RocketmqEvent>	originalChain = new ProxiedHandlerChain();
		// Execute the event handler chain.
		this.doHandler(new RocketmqEvent(msgExt, context.getMessageQueue()), originalChain);
	}

	@Override
	public void postHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception {
		
	}

	@Override
	public void afterCompletion(MessageExt msgExt, ConsumeOrderlyContext context, Exception ex) throws Exception {
		if(ex != null) {
			LOG.warn("Consume message failed. messageExt:{}", msgExt, ex);
		}
	}
	
}