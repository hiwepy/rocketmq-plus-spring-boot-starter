package org.apache.rocketmq.spring.boot;

public interface RocketMQListener<T> {
	
	void onMessage(T message);
	
}
