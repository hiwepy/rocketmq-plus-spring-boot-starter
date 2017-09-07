package org.apache.rocketmq.spring.boot.handler.chain;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

public interface HandlerChainResolver<T extends RocketmqEvent> {

	HandlerChain<T> getChain(T event , HandlerChain<T> originalChain);
	
}
