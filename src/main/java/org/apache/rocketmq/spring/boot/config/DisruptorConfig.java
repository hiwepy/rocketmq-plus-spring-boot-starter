package org.apache.rocketmq.spring.boot.config;


import org.apache.rocketmq.spring.boot.disruptor.RocketmqEventHandler;
import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;

import com.lmax.disruptor.EventHandler;

public class DisruptorConfig {

	private int ringBufferSize = 1024;
	private int ringThreadNumbers = 4;
	private boolean multiProducer = false;

	private RocketmqEventHandler eventHandler;
	private EventHandler<RocketmqDataEvent>[] preHandlers;
	private EventHandler<RocketmqDataEvent>[] postHandlers;

	public boolean isMultiProducer() {
		return multiProducer;
	}

	public void setMultiProducer(boolean multiProducer) {
		this.multiProducer = multiProducer;
	}

	public int getRingBufferSize() {
		return ringBufferSize;
	}

	public void setRingBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}

	public int getRingThreadNumbers() {
		return ringThreadNumbers;
	}

	public void setRingThreadNumbers(int ringThreadNumbers) {
		this.ringThreadNumbers = ringThreadNumbers;
	}

	public RocketmqEventHandler getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(RocketmqEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public EventHandler<RocketmqDataEvent>[] getPreHandlers() {
		return preHandlers;
	}

	public void setPreHandlers(EventHandler<RocketmqDataEvent>[] preHandlers) {
		this.preHandlers = preHandlers;
	}

	public EventHandler<RocketmqDataEvent>[] getPostHandlers() {
		return postHandlers;
	}

	public void setPostHandlers(EventHandler<RocketmqDataEvent>[] postHandlers) {
		this.postHandlers = postHandlers;
	}

}