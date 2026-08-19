package org.apache.rocketmq.spring.boot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;

/**
 * {@link EventHandler} base that wraps execution in pre-handle / post-handle /
 * after-completion advice, ensuring {@code afterCompletion} is always invoked.
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class AbstractAdviceMessageHandler<T extends RocketmqEvent> extends AbstractEnabledMessageHandler<T> {

	protected final Logger LOG = LoggerFactory.getLogger(AbstractAdviceMessageHandler.class);
	
    /**
     * <p>Pre handle.</p>
     * @param event
     * @return the pre handle
     */
	protected boolean preHandle(T event) throws Exception {
		return true;
	}

    /**
     * <p>Post handle.</p>
     * @param event
     */
	protected void postHandle(T event) throws Exception {
	}

    /**
     * <p>After completion.</p>
     * @param event
     * @param exception
     */
	public void afterCompletion(T event, Exception exception) throws Exception {
	}

    /**
     * <p>Execute chain.</p>
     * @param event
     * @param chain
     */
	protected void executeChain(T event, HandlerChain<T> chain) throws Exception {
		chain.doHandler(event);
	}

	@Override
    /**
     * <p>Performs handler internal.</p>
     * @param event
     * @param handlerChain
     */
	public void doHandlerInternal(T event, HandlerChain<T> handlerChain) throws Exception {

		if (!isEnabled(event)) {
        	LOG.debug("Handler '{}' is not enabled for the current event.  Proceeding without invoking this handler.", getName());
        	// Proceed without invoking this handler...
            handlerChain.doHandler(event);
		} else {
			
			LOG.trace("Handler '{}' enabled.  Executing now.", getName());
			
			Exception exception = null;
			
			try {
	
				boolean continueChain = preHandle(event);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Invoked preHandle method.  Continuing chain?: [" + continueChain + "]");
				}
				if (continueChain) {
					executeChain(event, handlerChain);
				}
				postHandle(event);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Successfully invoked postHandle method");
				}
	
			} catch (Exception e) {
				exception = e;
			} finally {
				cleanup(event, exception);
			}
		}

	}

    /**
     * <p>Cleanup.</p>
     * @param event
     * @param existing
     */
	protected void cleanup(T event, Exception existing) throws Exception {
		Exception exception = existing;
		try {
			afterCompletion(event, exception);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Successfully invoked afterCompletion method.");
			}
		} catch (Exception e) {
			if (exception == null) {
				exception = e;
			} else {
				LOG.debug("afterCompletion implementation threw an exception.  This will be ignored to "
						+ "allow the original source exception to be propagated.", e);
			}
		}
	}
	
    /**
     * <p>Checks if enabled.</p>
     * @param event
     * @return the is enabled
     */
	protected boolean isEnabled(T event)
			throws Exception {
		return isEnabled();
	}
	
    /**
     * <p>Checks if enabled.</p>
     * @return the is enabled
     */
	public boolean isEnabled() {
		return enabled;
	}

    /**
     * <p>Sets the enabled.</p>
     * @param enabled
     */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
