package org.apache.rocketmq.spring.boot.hooks;

import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;

public class MQPullConsumerScheduleShutdownHook extends Thread{
	
	private MQPullConsumerScheduleService consumerSchedule;
	
	public MQPullConsumerScheduleShutdownHook(MQPullConsumerScheduleService consumerSchedule) {
		this.consumerSchedule = consumerSchedule;
	}
	
	@Override
	public void run() {
		consumerSchedule.shutdown();
	}
	
}
