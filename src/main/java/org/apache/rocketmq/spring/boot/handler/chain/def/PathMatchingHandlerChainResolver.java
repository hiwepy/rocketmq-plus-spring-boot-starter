package org.apache.rocketmq.spring.boot.handler.chain.def;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver;

public class PathMatchingHandlerChainResolver implements HandlerChainResolver<RocketmqEvent> {

	private static final Logger log = LoggerFactory.getLogger(PathMatchingHandlerChainResolver.class);
	/**
	 * handlerChain管理器
	 */
	private HandlerChainManager<RocketmqEvent> handlerChainManager;
	
	/**
	 * 路径匹配器
	 */
	private PathMatcher pathMatcher;
	
	 public PathMatchingHandlerChainResolver() {
        this.pathMatcher = new AntPathMatcher();
        this.handlerChainManager = new DefaultHandlerChainManager();
    }

	public HandlerChainManager<RocketmqEvent> getHandlerChainManager() {
		return handlerChainManager;
	}

	public void setHandlerChainManager(HandlerChainManager<RocketmqEvent> handlerChainManager) {
		this.handlerChainManager = handlerChainManager;
	}

	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
	
	
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

    protected boolean pathMatches(String pattern, String path) {
        PathMatcher pathMatcher = getPathMatcher();
        return pathMatcher.match(pattern, path);
    }

    protected String getPathWithinEvent(RocketmqEvent event) {
    	return event.getRuleExpression();
    }
	
}
