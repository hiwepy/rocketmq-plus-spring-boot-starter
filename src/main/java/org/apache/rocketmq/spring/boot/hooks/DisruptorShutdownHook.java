package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;

import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorShutdownHook extends Thread{
	
	private Disruptor<RocketmqDataEvent> disruptor;
	
	public DisruptorShutdownHook(Disruptor<RocketmqDataEvent> disruptor) {
		this.disruptor = disruptor;
	}
	
	@Override
	public void run() {
		disruptor.shutdown();
	}
	
}
