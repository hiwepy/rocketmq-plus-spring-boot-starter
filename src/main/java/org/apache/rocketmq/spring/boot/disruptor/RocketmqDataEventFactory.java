package org.apache.rocketmq.spring.boot.disruptor;

import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;

import com.lmax.disruptor.EventFactory;

public class RocketmqDataEventFactory implements EventFactory<RocketmqDisruptorEvent> {

	@Override
	public RocketmqDisruptorEvent newInstance() {
		return new RocketmqDisruptorEvent(this);
	}
	
}
