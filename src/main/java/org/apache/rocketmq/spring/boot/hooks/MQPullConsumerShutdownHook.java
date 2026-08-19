package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.consumer.MQPullConsumer;

/**
 * JVM shutdown hook that shuts down a RocketMQ {@link MQPullConsumer},
 * releasing resources and unregistering from the broker.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
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
    /**
     * <p>Run.</p>
     */
	public void run() {
		consumer.shutdown();
	}

}
