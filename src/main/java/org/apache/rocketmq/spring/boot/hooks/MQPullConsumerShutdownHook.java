package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.consumer.MQPullConsumer;

/**
 * JVM shutdown hook that shuts down a RocketMQ {@link MQPullConsumer},
 * releasing resources and unregistering from the broker.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class MQPullConsumerShutdownHook extends Thread{

	/** The pull consumer to shut down. */
	private MQPullConsumer consumer;

	/**
	 * @param consumer the pull consumer to shut down
	 */
	public MQPullConsumerShutdownHook(MQPullConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void run() {
		consumer.shutdown();
	}

}
