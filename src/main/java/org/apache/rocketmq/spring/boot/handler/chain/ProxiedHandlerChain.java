package org.apache.rocketmq.spring.boot.handler.chain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;

public class ProxiedHandlerChain implements HandlerChain<RocketmqEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ProxiedHandlerChain.class);
	
    private ProxiedHandlerChain orig;
    private List<EventHandler<RocketmqEvent>> handlers;
    private int index = 0;

    public ProxiedHandlerChain(ProxiedHandlerChain orig, List<EventHandler<RocketmqEvent>> handlers) {
        if (orig == null) {
            throw new NullPointerException("original HandlerChain cannot be null.");
        }
        this.orig = orig;
        this.handlers = handlers;
        this.index = 0;
    }

    @Override
	public void onEvent(RocketmqEvent event) throws Exception {
        if (this.handlers == null || this.handlers.size() == this.index) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking original filter chain.");
            }
            this.orig.onEvent(event);
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking wrapped filter at index [" + this.index + "]");
            }
            this.handlers.get(this.index++).onEvent(event, this);
        }
    }
    
}
