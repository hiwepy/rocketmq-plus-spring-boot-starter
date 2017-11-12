package org.apache.rocketmq.spring.boot;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

/**
 * RocketMQ PushConsumer Lifecycle Listener
 */
public interface RocketMQPushConsumerLifecycleListener extends RocketMQConsumerLifecycleListener<DefaultMQPushConsumer> {
}
