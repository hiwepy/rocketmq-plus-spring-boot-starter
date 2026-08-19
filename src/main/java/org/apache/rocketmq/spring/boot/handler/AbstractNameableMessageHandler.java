package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

/**
 * Base {@link EventHandler} that implements {@link Nameable}, providing a
 * configurable name used by the handler-chain manager.
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public abstract class AbstractNameableMessageHandler<T extends RocketmqEvent> implements EventHandler<T>, Nameable {

	/** The unique handler name used for registration and logging. */
	protected String name;

	/** @return the unique handler name */
	protected String getName() {
		return this.name;
	}

	@Override
    /**
     * <p>Sets the name.</p>
     * @param name
     */
	public void setName(String name) {
		this.name = name;
	}

}
