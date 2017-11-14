package org.apache.rocketmq.spring.boot;

import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByRandoom;

public class RocketmqPushConsumerTemplate {

	public final MessageQueueSelector HASH_SELECTOR = new SelectMessageQueueByHash();
	public final MessageQueueSelector RANDOOM_SELECTOR = new SelectMessageQueueByRandoom();
	protected MQPushConsumer consumer;

	public RocketmqPushConsumerTemplate(MQPushConsumer consumer) {
		this.consumer = consumer;
	}

	public void registerMessageListener(final MessageListenerConcurrently messageListener){
		consumer.registerMessageListener(messageListener);
	}

	public void registerMessageListener(final MessageListenerOrderly messageListener){
		consumer.registerMessageListener(messageListener);
	}

	public MQPushConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(MQPushConsumer consumer) {
		this.consumer = consumer;
	}
	
}
