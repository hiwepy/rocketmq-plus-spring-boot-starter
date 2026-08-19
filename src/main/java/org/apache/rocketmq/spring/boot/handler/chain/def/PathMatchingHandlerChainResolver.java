package org.apache.rocketmq.spring.boot.handler.chain.def;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver;

/**
 * {@link HandlerChainResolver} that matches the event's route expression
 * against the registered chain names using an Ant-style {@link PathMatcher},
 * returning the first matching chain.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class PathMatchingHandlerChainResolver implements HandlerChainResolver<RocketmqEvent> {

	private static final Logger log = LoggerFactory.getLogger(PathMatchingHandlerChainResolver.class);
	/**
	 * The handler-chain manager used to look up chains.
	 */
	private HandlerChainManager<RocketmqEvent> handlerChainManager;

	/**
	 * Ant-style path matcher used to test route expressions.
	 */
	private PathMatcher pathMatcher;
	
	 public PathMatchingHandlerChainResolver() {
        this.pathMatcher = new AntPathMatcher();
        this.handlerChainManager = new DefaultHandlerChainManager();
    }

    /**
     * <p>Returns the handler chain manager.</p>
     * @return the get handler chain manager
     */
	public HandlerChainManager<RocketmqEvent> getHandlerChainManager() {
		return handlerChainManager;
	}

    /**
     * <p>Sets the handler chain manager.</p>
     * @param handlerChainManager
     */
	public void setHandlerChainManager(HandlerChainManager<RocketmqEvent> handlerChainManager) {
		this.handlerChainManager = handlerChainManager;
	}

    /**
     * <p>Returns the path matcher.</p>
     * @return the get path matcher
     */
	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

    /**
     * <p>Sets the path matcher.</p>
     * @param pathMatcher
     */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
	
	
    /**
     * <p>Returns the chain.</p>
     * @param event
     * @param originalChain
     * @return the get chain
     */
	public HandlerChain<RocketmqEvent> getChain(RocketmqEvent event, HandlerChain<RocketmqEvent> originalChain) {
        HandlerChainManager<RocketmqEvent> handlerChainManager = getHandlerChainManager();
        if (!handlerChainManager.hasChains()) {
            return null;
        }
        String eventURI = getPathWithinEvent(event);
        for (String pathPattern : handlerChainManager.getChainNames()) {
            if (pathMatches(pathPattern, eventURI)) {
                if (log.isTraceEnabled()) {
                    log.trace("Matched path pattern [" + pathPattern + "] for eventURI [" + eventURI + "].  " +
                            "Utilizing corresponding handler chain...");
                }
                return handlerChainManager.proxy(originalChain, pathPattern);
            }
        }
        return null;
    }

    /**
     * <p>Path matches.</p>
     * @param pattern
     * @param path
     * @return the path matches
     */
    protected boolean pathMatches(String pattern, String path) {
        PathMatcher pathMatcher = getPathMatcher();
        return pathMatcher.match(pattern, path);
    }

    /**
     * <p>Returns the path within event.</p>
     * @param event
     * @return the get path within event
     */
    protected String getPathWithinEvent(RocketmqEvent event) {
    	return event.getRouteExpression();
    }
	
}
