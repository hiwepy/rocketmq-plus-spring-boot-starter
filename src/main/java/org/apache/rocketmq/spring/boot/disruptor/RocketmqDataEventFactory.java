package org.apache.rocketmq.spring.boot.disruptor;

import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;

import com.lmax.disruptor.EventFactory;

public class RocketmqDataEventFactory implements EventFactory<RocketmqDataEvent> {

	@Override
	public RocketmqDataEvent newInstance() {
		return new RocketmqDataEvent();
	}
	
}
