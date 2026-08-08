package org.apache.rocketmq.spring.boot.handler.chain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;

/**
 * A {@link HandlerChain} that executes a list of {@link EventHandler}s in order
 * and then delegates to an optional original (root) chain.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class ProxiedHandlerChain implements HandlerChain<RocketmqEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ProxiedHandlerChain.class);

	/** The original chain to delegate to after this chain's handlers. */
    private ProxiedHandlerChain originalChain;
    /** The ordered handlers in this chain. */
    private List<EventHandler<RocketmqEvent>> handlers;
    /** Current position within the handler list. */
    private int currentPosition = 0;

    /** Creates an empty root chain. */
    public ProxiedHandlerChain() {
        this.currentPosition = -1;
    }

    /**
     * @param orig     the original chain to delegate to
     * @param handlers the ordered handlers to execute
     */
    public ProxiedHandlerChain(ProxiedHandlerChain orig, List<EventHandler<RocketmqEvent>> handlers) {
        if (orig == null) {
            throw new NullPointerException("original HandlerChain cannot be null.");
        }
        this.originalChain = orig;
        this.handlers = handlers;
        this.currentPosition = 0;
    }

    @Override
	public void doHandler(RocketmqEvent event) throws Exception {
        if (this.handlers == null || this.handlers.size() == this.currentPosition) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking original filter chain.");
            }
            if(this.originalChain != null) {
            	this.originalChain.doHandler(event);
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking wrapped filter at index [" + this.currentPosition + "]");
            }
            this.handlers.get(this.currentPosition++).doHandler(event, this);
        }
    }
    
}
