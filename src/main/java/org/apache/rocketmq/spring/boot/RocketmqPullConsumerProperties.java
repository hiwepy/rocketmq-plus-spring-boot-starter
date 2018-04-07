/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
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

@ConfigurationProperties(RocketmqPullConsumerProperties.PREFIX)
public class RocketmqPullConsumerProperties extends ClientConfig {
	
	/**
     * ConsumeType.CONSUME_PASSIVELY : "PULL"
     */
	public static final String PREFIX = "spring.rocketmq.consume-actively";
	
	/** 是否启用 **/
	private boolean enabled = false;
	
	/** 是否使用定时的Consumer  **/
	private boolean schedulable = false;
	
	/**
     * Do the same thing for the same Group, the application must be set,and
     * guarantee Globally unique
     */
    private String consumerGroup;

    /**
     * Consumption pattern,default is clustering
     * 消息模式
     * 广播模式消费： BROADCASTING
     * 集群模式消费： CLUSTERING
     */
    private String messageModel = "CLUSTERING";
    
    /**
     * Topic set you want to register
     */
    private Set<String> registerTopics = new HashSet<String>();
    
    /**
     * Long polling mode, the Consumer connection max suspend time, it is not
     * recommended to modify
     */
    private long brokerSuspendMaxTimeMillis = 1000 * 20;
    /**
     * Long polling mode, the Consumer connection timeout(must greater than
     * brokerSuspendMaxTimeMillis), it is not recommended to modify
     */
    private long consumerTimeoutMillisWhenSuspend = 1000 * 30;
    /**
     * The socket timeout in milliseconds
     */
    private long consumerPullTimeoutMillis = 1000 * 10;
    
    /**
     * Schedule Thread Nums for pull consumer
     */
    private int pullThreadNums = 20;
    
    private int pullNextDelayTimeMillis = 200;
    
    /**
     * Max re-consume times. -1 means 16 times.
     * </p>
     *
     * If messages are re-consumed more than {@link #maxReconsumeTimes} before success, it's be directed to a deletion
     * queue waiting.
     */
    private int maxReconsumeTimes = -1;
    
	/**
	 * 延迟启动时间，单位秒，主要是等待spring事件监听相关程序初始化完成，否则，会出现对RocketMQ的消息进行消费后立即发布消息到达的事件，然而此事件的监听程序还未初始化，从而造成消息的丢失
	 */
	private int delayStartSeconds = 10;
	
    
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isSchedulable() {
		return schedulable;
	}

	public void setSchedulable(boolean schedulable) {
		this.schedulable = schedulable;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public String getMessageModel() {
		return messageModel;
	}

	public void setMessageModel(String messageModel) {
		this.messageModel = messageModel;
	}

	public Set<String> getRegisterTopics() {
		return registerTopics;
	}

	public void setRegisterTopics(Set<String> registerTopics) {
		this.registerTopics = registerTopics;
	}

	public long getBrokerSuspendMaxTimeMillis() {
		return brokerSuspendMaxTimeMillis;
	}

	public void setBrokerSuspendMaxTimeMillis(long brokerSuspendMaxTimeMillis) {
		this.brokerSuspendMaxTimeMillis = brokerSuspendMaxTimeMillis;
	}

	public long getConsumerTimeoutMillisWhenSuspend() {
		return consumerTimeoutMillisWhenSuspend;
	}

	public void setConsumerTimeoutMillisWhenSuspend(long consumerTimeoutMillisWhenSuspend) {
		this.consumerTimeoutMillisWhenSuspend = consumerTimeoutMillisWhenSuspend;
	}

	public long getConsumerPullTimeoutMillis() {
		return consumerPullTimeoutMillis;
	}

	public void setConsumerPullTimeoutMillis(long consumerPullTimeoutMillis) {
		this.consumerPullTimeoutMillis = consumerPullTimeoutMillis;
	}

	public int getPullThreadNums() {
		return pullThreadNums;
	}

	public void setPullThreadNums(int pullThreadNums) {
		this.pullThreadNums = pullThreadNums;
	}

	public int getPullNextDelayTimeMillis() {
		return pullNextDelayTimeMillis;
	}

	public void setPullNextDelayTimeMillis(int pullNextDelayTimeMillis) {
		this.pullNextDelayTimeMillis = pullNextDelayTimeMillis;
	}

	public int getMaxReconsumeTimes() {
		return maxReconsumeTimes;
	}

	public void setMaxReconsumeTimes(int maxReconsumeTimes) {
		this.maxReconsumeTimes = maxReconsumeTimes;
	}
	
	public int getDelayStartSeconds() {
		return delayStartSeconds;
	}

	public void setDelayStartSeconds(int delayStartSeconds) {
		this.delayStartSeconds = delayStartSeconds;
	}
	
	
}
