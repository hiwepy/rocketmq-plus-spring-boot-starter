package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;

/**
 */
public interface EventHandler<T extends RocketmqEvent> {

	public void onEvent(T event, HandlerChain<T> handlerChain) throws Exception;
	
}
