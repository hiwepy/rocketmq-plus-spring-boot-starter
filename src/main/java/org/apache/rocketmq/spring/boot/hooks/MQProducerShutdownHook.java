package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.producer.MQProducer;

/**
 * JVM shutdown hook that shuts down a RocketMQ {@link MQProducer}, releasing
 * resources and unregistering from the broker.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class MQProducerShutdownHook extends Thread{

	/** The producer to shut down. */
	private MQProducer producer;

	/**
	 * @param producer the producer to shut down
	 */
	public MQProducerShutdownHook(MQProducer producer) {
		this.producer = producer;
	}

	@Override
	public void run() {
		producer.shutdown();
	}

}
