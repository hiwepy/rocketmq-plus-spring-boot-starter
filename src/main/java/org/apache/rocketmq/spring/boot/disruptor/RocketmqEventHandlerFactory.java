package org.apache.rocketmq.spring.boot.disruptor;

import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;

import com.lmax.disruptor.EventHandler;

public class RocketmqEventHandlerFactory implements EventHandlerFactory<RocketmqDataEvent> {

	@Override
	public EventHandler<RocketmqDataEvent>[] getPreHandlers() {
		return null;
	}

	@Override
	public EventHandler<RocketmqDataEvent>[] getPostHandlers() {
		return null;
	}

}
