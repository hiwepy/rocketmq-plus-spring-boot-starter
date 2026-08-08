package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;

/**
 * JVM shutdown hook that shuts down a RocketMQ
 * {@link MQPullConsumerScheduleService}, releasing resources and unregistering
 * from the broker.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class MQPullConsumerScheduleShutdownHook extends Thread{

	/** The scheduled pull consumer service to shut down. */
	private MQPullConsumerScheduleService consumerSchedule;

	/**
	 * @param consumerSchedule the scheduled pull consumer service to shut down
	 */
	public MQPullConsumerScheduleShutdownHook(MQPullConsumerScheduleService consumerSchedule) {
		this.consumerSchedule = consumerSchedule;
	}

	@Override
	public void run() {
		consumerSchedule.shutdown();
	}

}
