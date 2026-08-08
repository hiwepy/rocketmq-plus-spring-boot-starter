package org.apache.rocketmq.spring.boot.handler;

import java.util.List;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;


/**
 * A named, ordered list of {@link EventHandler} instances that can be combined
 * into a single {@link HandlerChain}.
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface NamedHandlerList<T extends RocketmqEvent> extends List<EventHandler<T>> {

	/**
	 * Returns the configuration-unique name assigned to this {@code Handler} list.
	 *
	 * @return the unique name of this handler list
	 */
	String getName();

	/**
	 * Returns a new {@link HandlerChain} instance that will first execute this
	 * list's handlers (in list order) and end with the execution of the given
	 * {@code handlerChain} instance.
	 *
	 * @param handlerChain the chain to delegate to after this list's handlers
	 * @return a proxied handler chain
	 */
	HandlerChain<T> proxy(HandlerChain<T> handlerChain);

}
