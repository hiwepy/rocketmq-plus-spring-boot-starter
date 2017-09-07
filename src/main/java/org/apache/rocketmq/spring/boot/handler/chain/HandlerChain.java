package org.apache.rocketmq.spring.boot.handler.chain;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

public interface HandlerChain<T extends RocketmqEvent>{

	void onEvent(T event) throws Exception;
	
}
