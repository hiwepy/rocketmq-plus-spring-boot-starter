package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

/**
 * Strategy for resolving the {@link EventHandler} bound to a given dispatch
 * path (e.g. {@code topic/tags/keys}).
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface PathProcessor<T extends RocketmqEvent> {

	/**
	 * Resolves the handler associated with the given path.
	 *
	 * @param path the dispatch path (e.g. {@code topic/tags/keys})
	 * @return the matching handler, or {@code null} if none is bound
	 */
	EventHandler<T> processPath(String path);

}
