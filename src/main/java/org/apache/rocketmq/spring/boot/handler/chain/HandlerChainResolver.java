package org.apache.rocketmq.spring.boot.handler.chain;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

/**
 * Strategy for resolving the {@link HandlerChain} to execute for a given event.
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface HandlerChainResolver<T extends RocketmqEvent> {

	/**
	 * Resolves the handler chain for the event, wrapping the original chain.
	 *
	 * @param event         the event being processed
	 * @param originalChain the original (root) chain to delegate to
	 * @return the resolved handler chain
	 */
	HandlerChain<T> getChain(T event , HandlerChain<T> originalChain);

}
