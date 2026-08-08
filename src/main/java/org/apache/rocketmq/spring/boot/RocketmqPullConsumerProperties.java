/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.rocketmq.spring.boot;

import java.util.HashSet;
import java.util.Set;

import org.apache.rocketmq.client.ClientConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the RocketMQ <strong>pull</strong> consumer.
 * <p>
 * Bound to the {@code rocketmq.consume-actively.*} namespace and extends the
 * native RocketMQ {@link ClientConfig} so every client-level option is
 * available. Pull consumers actively fetch messages from the broker, either
 * directly ({@link org.apache.rocketmq.client.consumer.DefaultMQPullConsumer})
 * or via a scheduled service
 * ({@link org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService}).
 * </p>
 *
 * <h3>Configuration keys</h3>
 * <ul>
 *   <li>{@code rocketmq.consume-actively.enabled} — opt-in switch (default {@code false})</li>
 *   <li>{@code rocketmq.consume-actively.schedulable} — use the scheduled pull service (default {@code false})</li>
 *   <li>{@code rocketmq.consume-actively.consumer-group} — globally unique consumer group (required)</li>
 *   <li>{@code rocketmq.consume-actively.namesrv-addr} — name server address (required)</li>
 *   <li>{@code rocketmq.consume-actively.message-model} — {@code CLUSTERING} or {@code BROADCASTING} (default {@code CLUSTERING})</li>
 *   <li>{@code rocketmq.consume-actively.register-topics} — topics to register</li>
 *   <li>{@code rocketmq.consume-actively.delay-start-seconds} — delayed start in seconds (default {@code 10})</li>
 * </ul>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@ConfigurationProperties(RocketmqPullConsumerProperties.PREFIX)
public class RocketmqPullConsumerProperties extends ClientConfig {
	
	/**
     * Configuration prefix. {@code CONSUME_PASSIVELY} corresponds to the "PULL"
     * consume type.
     */
	public static final String PREFIX = "rocketmq.consume-actively";
	
	/** Whether the pull consumer auto-configuration is enabled. */
	private boolean enabled = false;
	
	/** Whether to use the scheduled pull consumer service. */
	private boolean schedulable = false;
	
    /**
     * Consumer group name; must be globally unique and shared by consumers
     * performing the same role.
     */
    private String consumerGroup;

    /**
     * Message delivery model. {@code BROADCASTING} delivers every message to
     * every consumer; {@code CLUSTERING} load-balances messages across the
     * consumer group. Defaults to {@code CLUSTERING}.
     */
    private String messageModel = "CLUSTERING";
    
    /** Topics the consumer should register with the name server. */
    private Set<String> registerTopics = new HashSet<String>();
    
    /**
     * Long-polling maximum suspend time for the broker side (milliseconds).
     * Not recommended to modify. Defaults to {@code 20000}.
     */
    private long brokerSuspendMaxTimeMillis = 1000 * 20;
    /**
     * Long-polling consumer-side timeout; must be greater than
     * {@link #brokerSuspendMaxTimeMillis}. Defaults to {@code 30000}.
     */
    private long consumerTimeoutMillisWhenSuspend = 1000 * 30;
    /** Socket timeout for a single pull request in milliseconds. */
    private long consumerPullTimeoutMillis = 1000 * 10;
    
    /** Number of pull threads used by the scheduled pull consumer service. */
    private int pullThreadNums = 20;
    
    /** Delay before the next pull attempt in milliseconds. */
    private int pullNextDelayTimeMillis = 200;
    
    /**
     * Max re-consume times. {@code -1} means 16 times. Messages exceeding this
     * count are directed to a deletion queue.
     */
    private int maxReconsumeTimes = -1;
    
	/**
	 * Delayed consumer start in seconds. Lets Spring event listeners initialise
	 * before consumption begins, avoiding message loss when a consume-arrived
	 * event is published before its listener is ready.
	 */
	private int delayStartSeconds = 10;
	
    
	/** @return {@code true} if the pull consumer is enabled */
	public boolean isEnabled() {
		return enabled;
	}

	/** @param enabled whether to enable the pull consumer */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/** @return {@code true} if the scheduled pull service is used */
	public boolean isSchedulable() {
		return schedulable;
	}

	/** @param schedulable whether to use the scheduled pull service */
	public void setSchedulable(boolean schedulable) {
		this.schedulable = schedulable;
	}

	/** @return the consumer group name */
	public String getConsumerGroup() {
		return consumerGroup;
	}

	/** @param consumerGroup the consumer group name */
	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	/** @return the message delivery model name */
	public String getMessageModel() {
		return messageModel;
	}

	/** @param messageModel the message delivery model name */
	public void setMessageModel(String messageModel) {
		this.messageModel = messageModel;
	}

	/** @return the set of topics to register */
	public Set<String> getRegisterTopics() {
		return registerTopics;
	}

	/** @param registerTopics the set of topics to register */
	public void setRegisterTopics(Set<String> registerTopics) {
		this.registerTopics = registerTopics;
	}

	/** @return the broker-side maximum suspend time in milliseconds */
	public long getBrokerSuspendMaxTimeMillis() {
		return brokerSuspendMaxTimeMillis;
	}

	/** @param brokerSuspendMaxTimeMillis the broker-side maximum suspend time in milliseconds */
	public void setBrokerSuspendMaxTimeMillis(long brokerSuspendMaxTimeMillis) {
		this.brokerSuspendMaxTimeMillis = brokerSuspendMaxTimeMillis;
	}

	/** @return the consumer-side suspend timeout in milliseconds */
	public long getConsumerTimeoutMillisWhenSuspend() {
		return consumerTimeoutMillisWhenSuspend;
	}

	/** @param consumerTimeoutMillisWhenSuspend the consumer-side suspend timeout in milliseconds */
	public void setConsumerTimeoutMillisWhenSuspend(long consumerTimeoutMillisWhenSuspend) {
		this.consumerTimeoutMillisWhenSuspend = consumerTimeoutMillisWhenSuspend;
	}

	/** @return the pull socket timeout in milliseconds */
	public long getConsumerPullTimeoutMillis() {
		return consumerPullTimeoutMillis;
	}

	/** @param consumerPullTimeoutMillis the pull socket timeout in milliseconds */
	public void setConsumerPullTimeoutMillis(long consumerPullTimeoutMillis) {
		this.consumerPullTimeoutMillis = consumerPullTimeoutMillis;
	}

	/** @return the number of pull threads */
	public int getPullThreadNums() {
		return pullThreadNums;
	}

	/** @param pullThreadNums the number of pull threads */
	public void setPullThreadNums(int pullThreadNums) {
		this.pullThreadNums = pullThreadNums;
	}

	/** @return the delay before the next pull in milliseconds */
	public int getPullNextDelayTimeMillis() {
		return pullNextDelayTimeMillis;
	}

	/** @param pullNextDelayTimeMillis the delay before the next pull in milliseconds */
	public void setPullNextDelayTimeMillis(int pullNextDelayTimeMillis) {
		this.pullNextDelayTimeMillis = pullNextDelayTimeMillis;
	}

	/** @return the max re-consume times */
	public int getMaxReconsumeTimes() {
		return maxReconsumeTimes;
	}

	/** @param maxReconsumeTimes the max re-consume times */
	public void setMaxReconsumeTimes(int maxReconsumeTimes) {
		this.maxReconsumeTimes = maxReconsumeTimes;
	}
	
	/** @return the delayed start in seconds */
	public int getDelayStartSeconds() {
		return delayStartSeconds;
	}

	/** @param delayStartSeconds the delayed start in seconds */
	public void setDelayStartSeconds(int delayStartSeconds) {
		this.delayStartSeconds = delayStartSeconds;
	}
	
	
}
