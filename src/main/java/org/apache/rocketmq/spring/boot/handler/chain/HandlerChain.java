package org.apache.rocketmq.spring.boot.handler.chain;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

/**
 * A chain of {@link EventHandler}s invoked in sequence for a single event.
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface HandlerChain<T extends RocketmqEvent>{

	/**
	 * Continues processing the event down the chain.
	 *
	 * @param event the event to handle
	 * @throws Exception if a handler in the chain fails
	 */
	void doHandler(T event) throws Exception;

}
