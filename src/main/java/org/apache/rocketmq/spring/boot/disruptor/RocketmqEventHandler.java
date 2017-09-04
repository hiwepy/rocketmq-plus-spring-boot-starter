package org.apache.rocketmq.spring.boot.disruptor;

import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;

import com.lmax.disruptor.EventHandler;

public abstract class RocketmqEventHandler implements EventHandler<RocketmqDataEvent> {

	protected EventHandler<RocketmqDataEvent> next;
	
	public RocketmqEventHandler() {
		this.next = null;
	}
	
	public RocketmqEventHandler(EventHandler<RocketmqDataEvent> next) {
		this.next = next;
	}

	public EventHandler<RocketmqDataEvent> getNext() {
		return next;
	}

	public void setNext(EventHandler<RocketmqDataEvent> next) {
		this.next = next;
	}
	
}
