package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.consumer.MQPushConsumer;

/**
 * JVM shutdown hook that shuts down a RocketMQ {@link MQPushConsumer},
 * releasing resources and unregistering from the broker.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class MQPushConsumerShutdownHook extends Thread{

	/** The push consumer to shut down. */
	private MQPushConsumer consumer;

	/**
	 * @param consumer the push consumer to shut down
	 */
	public MQPushConsumerShutdownHook(MQPushConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void run() {
		consumer.shutdown();
	}

}
