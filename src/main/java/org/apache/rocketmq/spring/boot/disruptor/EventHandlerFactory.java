package org.apache.rocketmq.spring.boot.disruptor;

import com.lmax.disruptor.EventHandler;

public interface EventHandlerFactory<T> {

	public RocketmqEventHandler getEventHandler();
	
	public EventHandler<T>[] getPreHandlers();
	
	public EventHandler<T>[] getPostHandlers();
	
}
