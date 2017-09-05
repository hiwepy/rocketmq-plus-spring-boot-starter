package org.apache.rocketmq.spring.boot;

import org.apache.rocketmq.spring.boot.config.ConsumerConfig;
import org.apache.rocketmq.spring.boot.config.DisruptorConfig;
import org.apache.rocketmq.spring.boot.config.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(RocketmqProperties.PREFIX)
public class RocketmqProperties {

	public static final String PREFIX = "spring.rocketmq";
	public static final String PRODUCER_PREFIX = PREFIX + ".producer";
	public static final String CONSUMER_PREFIX = PREFIX + ".consumer";
	public static final String DISRUPTOR_PREFIX = PREFIX + ".disruptor";

	@NestedConfigurationProperty
	private ProducerConfig producer;
	@NestedConfigurationProperty
	private ConsumerConfig consumer;
	@NestedConfigurationProperty
	private DisruptorConfig disruptor;

	public ProducerConfig getProducer() {
		return producer;
	}

	public void setProducer(ProducerConfig producer) {
		this.producer = producer;
	}

	public ConsumerConfig getConsumer() {
		return consumer;
	}

	public void setConsumer(ConsumerConfig consumer) {
		this.consumer = consumer;
	}

	public DisruptorConfig getDisruptor() {
		return disruptor;
	}

	public void setDisruptor(DisruptorConfig disruptor) {
		this.disruptor = disruptor;
	}

}