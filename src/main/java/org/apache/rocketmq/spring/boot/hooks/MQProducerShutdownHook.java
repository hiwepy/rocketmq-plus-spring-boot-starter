package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.producer.MQProducer;

public class MQProducerShutdownHook extends Thread{
	
	private MQProducer producer;
	
	public MQProducerShutdownHook(MQProducer producer) {
		this.producer = producer;
	}
	
	@Override
	public void run() {
		producer.shutdown();
	}
	
}
