package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.consumer.MQPullConsumer;

public class MQPullConsumerShutdownHook extends Thread{
	
	private MQPullConsumer consumer;
	
	public MQPullConsumerShutdownHook(MQPullConsumer consumer) {
		this.consumer = consumer;
	}
	
	@Override
	public void run() {
		consumer.shutdown();
	}
	
}
