package org.apache.rocketmq.spring.boot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;

/**
 * Base {@link EventHandler} that can be enabled or disabled at runtime.
 * <p>When disabled the handler is skipped and the chain continues.</p>
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public abstract class AbstractEnabledMessageHandler<T extends RocketmqEvent> extends AbstractNameableMessageHandler<T> {

	protected final Logger LOG = LoggerFactory.getLogger(AbstractEnabledMessageHandler.class);
	/** Whether this handler is enabled (default {@code true}). */
	protected boolean enabled = true;

	/**
	 * Subclass hook invoked when the handler is enabled for the current event.
	 *
	 * @param event        the event to handle
	 * @param handlerChain the current handler chain
	 * @throws Exception if handling fails
	 */
	protected abstract void doHandlerInternal(T event, HandlerChain<T> handlerChain) throws Exception;

	@Override
	public void doHandler(T event, HandlerChain<T> handlerChain) throws Exception {

		if (!isEnabled(event)) {
			LOG.debug("Handler '{}' is not enabled for the current event.  Proceeding without invoking this handler.",
					getName());
			// Proceed without invoking this handler...
			handlerChain.doHandler(event);
		} else {
			LOG.trace("Handler '{}' enabled.  Executing now.", getName());
			doHandlerInternal(event, handlerChain);
		}

	}

	/**
	 * @param event the current event
	 * @return {@code true} if the handler is enabled for the event
	 * @throws Exception if the enabled check fails
	 */
	protected boolean isEnabled(T event) throws Exception {
		return isEnabled();
	}

	/** @return {@code true} if the handler is enabled */
	public boolean isEnabled() {
		return enabled;
	}

	/** @param enabled whether the handler is enabled */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}



}
