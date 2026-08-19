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

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.spring.boot.enums.ConsumeMode;
import org.apache.rocketmq.spring.boot.enums.SelectorType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the RocketMQ <strong>push</strong> consumer.
 * <p>
 * Bound to the {@code rocketmq.consume-passively.*} namespace and extends the
 * native RocketMQ {@link ClientConfig}. Push consumers receive messages
 * delivered by the broker via long polling and process them through registered
 * message listeners (concurrently or orderly).
 * </p>
 *
 * <h3>Configuration keys</h3>
 * <ul>
 *   <li>{@code rocketmq.consume-passively.enabled} — opt-in switch (default {@code false})</li>
 *   <li>{@code rocketmq.consume-passively.consumer-group} — globally unique consumer group (required)</li>
 *   <li>{@code rocketmq.consume-passively.namesrv-addr} — name server address (required)</li>
 *   <li>{@code rocketmq.consume-passively.consume-mode} — {@code CONCURRENTLY} or {@code ORDERLY} (default {@code CONCURRENTLY})</li>
 *   <li>{@code rocketmq.consume-passively.selector-type} — {@code TAG} or {@code SQL92} (default {@code TAG})</li>
 *   <li>{@code rocketmq.consume-passively.subscription} — topic {@code ->} selector expression map</li>
 *   <li>{@code rocketmq.consume-passively.delay-start-seconds} — delayed start in seconds (default {@code 10})</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@ConfigurationProperties(RocketmqPushConsumerProperties.PREFIX)
public class RocketmqPushConsumerProperties extends ClientConfig {
	
	/**
     * Configuration prefix. {@code CONSUME_PASSIVELY} corresponds to the "PUSH"
     * consume type.
     */
	public static final String PREFIX = "rocketmq.consume-passively";
	
	/** Whether the push consumer auto-configuration is enabled. */
	private boolean enabled = false;
	
	/**
     * Consumers of the same role is required to have exactly same subscriptions and consumerGroup to correctly achieve
     * load balance. It's required and needs to be globally unique.
     * </p>
     *
     * See <a href="http://rocketmq.incubator.apache.org/docs/core-concept/">here</a> for further discussion.
     */
    private String consumerGroup;

    /**
     * Message model defines the way how messages are delivered to each consumer clients.
     * </p>
     *
     * RocketMQ supports two message models: clustering and broadcasting. If clustering is set, consumer clients with
     * the same {@link #consumerGroup} would only consume shards of the messages subscribed, which achieves load
     * balances; Conversely, if the broadcasting is set, each consumer client will consume all subscribed messages
     * separately.
     * 
     * BROADCASTING
     * CLUSTERING
     * </p>
     *
     * This field defaults to clustering.
     */
    private String messageModel = "CLUSTERING";
    
    /**
     * Consuming point on consumer booting.
     * </p>
     *
     * There are three consuming points:
     * <ul>
     *     <li>
     *         <code>CONSUME_FROM_LAST_OFFSET</code>: consumer clients pick up where it stopped previously.
     *         If it were a newly booting up consumer client, according aging of the consumer group, there are two
     *         cases:
     *         <ol>
     *             <li>
     *                 if the consumer group is created so recently that the earliest message being subscribed has yet
     *                 expired, which means the consumer group represents a lately launched business, consuming will
     *                 start from the very beginning;
     *             </li>
     *             <li>
     *                 if the earliest message being subscribed has expired, consuming will start from the latest
     *                 messages, meaning messages born prior to the booting timestamp would be ignored.
     *             </li>
     *         </ol>
     *     </li>
     *     <li>
     *         <code>CONSUME_FROM_FIRST_OFFSET</code>: Consumer client will start from earliest messages available.
     *     </li>
     *     <li>
     *         <code>CONSUME_FROM_TIMESTAMP</code>: Consumer client will start from specified timestamp, which means
     *         messages born prior to {@link #consumeTimestamp} will be ignored
     *     </li>
     * </ul>
     */
    //private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
	private String consumeFromWhere = "CONSUME_FROM_LAST_OFFSET";
    
	/**
     * Backtracking consumption time with second precision. Time format is
     * 20131223171201<br>
     * Implying Seventeen twelve and 01 seconds on December 23, 2013 year<br>
     * Default backtracking consumption time Half an hour ago.
     */
    private String consumeTimestamp = UtilAll.timeMillisToHumanString3(System.currentTimeMillis() - (1000 * 60 * 30));

    /**
     * Consume mode.
     * <ul>
     *   <li>{@code CONCURRENTLY} — consume using a thread pool</li>
     *   <li>{@code ORDERLY} — consume using a single thread per queue</li>
     * </ul>
     */
    private ConsumeMode consumeMode = ConsumeMode.CONCURRENTLY;
    
    /**
     * SelectorType
     */
    private SelectorType selectorType = SelectorType.TAG;
    
    /**
     * Subscription relationship
     */
    private Map<String /* topic */, String /* selectorExpress */> subscription = new HashMap<String, String>();
    
    /**
     * Minimum consumer thread number
     */
    private int consumeThreadMin = 20;

    /**
     * Max consumer thread number
     */
    private int consumeThreadMax = 64;

    /**
     * Threshold for dynamic adjustment of the number of thread pool
     */
    private long adjustThreadPoolNumsThreshold = 100000;

    /**
     * Concurrently max span offset.it has no effect on sequential consumption
     */
    private int consumeConcurrentlyMaxSpan = 2000;

    /**
     * Flow control threshold
     */
    private int pullThresholdForQueue = 1000;

    /**
     * Message pull Interval
     */
    private long pullInterval = 0;

    /**
     * Batch consumption size
     */
    private int consumeMessageBatchMaxSize = 1;

    /**
     * Batch pull size
     */
    private int pullBatchSize = 32;

    /**
     * Whether update subscription relationship when every pull
     */
    private boolean postSubscriptionWhenPull = false;

    /**
     * Max re-consume times. -1 means 16 times.
     * </p>
     *
     * If messages are re-consumed more than {@link #maxReconsumeTimes} before success, it's be directed to a deletion
     * queue waiting.
     */
    private int maxReconsumeTimes = -1;

    /**
     * Suspending pulling time for cases requiring slow pulling like flow-control scenario.
     */
    private long suspendCurrentQueueTimeMillis = 1000;

    /**
     * Maximum amount of time in minutes a message may block the consuming thread.
     */
    private long consumeTimeout = 15;
    
    /**
	 * Maximum number of retry to perform internally before claiming consume failure.
	 */
	private int retryTimesWhenConsumeFailed = 3;
	 /**
     * Message consume retry strategy<br> 
     * -1,no retry,put into DLQ directly<br> 
     * 0,broker control retry frequency<br>
     * >0,client control retry frequency
     */
	private int delayLevelWhenNextConsume = 0;
    
	/**
	 * Delay before the consumer starts, in seconds. Lets Spring event listeners
	 * finish initialising before messages are consumed, avoiding message loss
	 * when a message-arrived event is published before its listener is ready.
	 */
	private int delayStartSeconds = 10;
	
    
    /**
     * <p>Checks if enabled.</p>
     * @return the is enabled
     */
	public boolean isEnabled() {
		return enabled;
	}

    /**
     * <p>Sets the enabled.</p>
     * @param enabled
     */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
    /**
     * <p>Returns the consumer group.</p>
     * @return the get consumer group
     */
	public String getConsumerGroup() {
		return consumerGroup;
	}

    /**
     * <p>Sets the consumer group.</p>
     * @param consumerGroup
     */
	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

    /**
     * <p>Returns the message model.</p>
     * @return the get message model
     */
	public String getMessageModel() {
		return messageModel;
	}

    /**
     * <p>Sets the message model.</p>
     * @param messageModel
     */
	public void setMessageModel(String messageModel) {
		this.messageModel = messageModel;
	}

    /**
     * <p>Returns the consume from where.</p>
     * @return the get consume from where
     */
	public String getConsumeFromWhere() {
		return consumeFromWhere;
	}

    /**
     * <p>Sets the consume from where.</p>
     * @param consumeFromWhere
     */
	public void setConsumeFromWhere(String consumeFromWhere) {
		this.consumeFromWhere = consumeFromWhere;
	}

    /**
     * <p>Returns the consume timestamp.</p>
     * @return the get consume timestamp
     */
	public String getConsumeTimestamp() {
		return consumeTimestamp;
	}

    /**
     * <p>Sets the consume timestamp.</p>
     * @param consumeTimestamp
     */
	public void setConsumeTimestamp(String consumeTimestamp) {
		this.consumeTimestamp = consumeTimestamp;
	}
	
    /**
     * <p>Returns the consume mode.</p>
     * @return the get consume mode
     */
	public ConsumeMode getConsumeMode() {
		return consumeMode;
	}

    /**
     * <p>Sets the consume mode.</p>
     * @param consumeMode
     */
	public void setConsumeMode(ConsumeMode consumeMode) {
		this.consumeMode = consumeMode;
	}

    /**
     * <p>Returns the selector type.</p>
     * @return the get selector type
     */
	public SelectorType getSelectorType() {
		return selectorType;
	}

    /**
     * <p>Sets the selector type.</p>
     * @param selectorType
     */
	public void setSelectorType(SelectorType selectorType) {
		this.selectorType = selectorType;
	}

    /**
     * <p>Returns the subscription.</p>
     * @return the get subscription
     */
	public Map<String, String> getSubscription() {
		return subscription;
	}

    /**
     * <p>Sets the subscription.</p>
     * @param subscription
     */
	public void setSubscription(Map<String, String> subscription) {
		this.subscription = subscription;
	}

    /**
     * <p>Returns the consume thread min.</p>
     * @return the get consume thread min
     */
	public int getConsumeThreadMin() {
		return consumeThreadMin;
	}

    /**
     * <p>Sets the consume thread min.</p>
     * @param consumeThreadMin
     */
	public void setConsumeThreadMin(int consumeThreadMin) {
		this.consumeThreadMin = consumeThreadMin;
	}

    /**
     * <p>Returns the consume thread max.</p>
     * @return the get consume thread max
     */
	public int getConsumeThreadMax() {
		return consumeThreadMax;
	}

    /**
     * <p>Sets the consume thread max.</p>
     * @param consumeThreadMax
     */
	public void setConsumeThreadMax(int consumeThreadMax) {
		this.consumeThreadMax = consumeThreadMax;
	}

    /**
     * <p>Returns the adjust thread pool nums threshold.</p>
     * @return the get adjust thread pool nums threshold
     */
	public long getAdjustThreadPoolNumsThreshold() {
		return adjustThreadPoolNumsThreshold;
	}

    /**
     * <p>Sets the adjust thread pool nums threshold.</p>
     * @param adjustThreadPoolNumsThreshold
     */
	public void setAdjustThreadPoolNumsThreshold(long adjustThreadPoolNumsThreshold) {
		this.adjustThreadPoolNumsThreshold = adjustThreadPoolNumsThreshold;
	}

    /**
     * <p>Returns the consume concurrently max span.</p>
     * @return the get consume concurrently max span
     */
	public int getConsumeConcurrentlyMaxSpan() {
		return consumeConcurrentlyMaxSpan;
	}

    /**
     * <p>Sets the consume concurrently max span.</p>
     * @param consumeConcurrentlyMaxSpan
     */
	public void setConsumeConcurrentlyMaxSpan(int consumeConcurrentlyMaxSpan) {
		this.consumeConcurrentlyMaxSpan = consumeConcurrentlyMaxSpan;
	}

    /**
     * <p>Returns the pull threshold for queue.</p>
     * @return the get pull threshold for queue
     */
	public int getPullThresholdForQueue() {
		return pullThresholdForQueue;
	}

    /**
     * <p>Sets the pull threshold for queue.</p>
     * @param pullThresholdForQueue
     */
	public void setPullThresholdForQueue(int pullThresholdForQueue) {
		this.pullThresholdForQueue = pullThresholdForQueue;
	}

    /**
     * <p>Returns the pull interval.</p>
     * @return the get pull interval
     */
	public long getPullInterval() {
		return pullInterval;
	}

    /**
     * <p>Sets the pull interval.</p>
     * @param pullInterval
     */
	public void setPullInterval(long pullInterval) {
		this.pullInterval = pullInterval;
	}

    /**
     * <p>Returns the consume message batch max size.</p>
     * @return the get consume message batch max size
     */
	public int getConsumeMessageBatchMaxSize() {
		return consumeMessageBatchMaxSize;
	}

    /**
     * <p>Sets the consume message batch max size.</p>
     * @param consumeMessageBatchMaxSize
     */
	public void setConsumeMessageBatchMaxSize(int consumeMessageBatchMaxSize) {
		this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
	}

    /**
     * <p>Returns the pull batch size.</p>
     * @return the get pull batch size
     */
	public int getPullBatchSize() {
		return pullBatchSize;
	}

    /**
     * <p>Sets the pull batch size.</p>
     * @param pullBatchSize
     */
	public void setPullBatchSize(int pullBatchSize) {
		this.pullBatchSize = pullBatchSize;
	}

    /**
     * <p>Checks if post subscription when pull.</p>
     * @return the is post subscription when pull
     */
	public boolean isPostSubscriptionWhenPull() {
		return postSubscriptionWhenPull;
	}

    /**
     * <p>Sets the post subscription when pull.</p>
     * @param postSubscriptionWhenPull
     */
	public void setPostSubscriptionWhenPull(boolean postSubscriptionWhenPull) {
		this.postSubscriptionWhenPull = postSubscriptionWhenPull;
	}

    /**
     * <p>Returns the max reconsume times.</p>
     * @return the get max reconsume times
     */
	public int getMaxReconsumeTimes() {
		return maxReconsumeTimes;
	}

    /**
     * <p>Sets the max reconsume times.</p>
     * @param maxReconsumeTimes
     */
	public void setMaxReconsumeTimes(int maxReconsumeTimes) {
		this.maxReconsumeTimes = maxReconsumeTimes;
	}

    /**
     * <p>Returns the suspend current queue time millis.</p>
     * @return the get suspend current queue time millis
     */
	public long getSuspendCurrentQueueTimeMillis() {
		return suspendCurrentQueueTimeMillis;
	}

    /**
     * <p>Sets the suspend current queue time millis.</p>
     * @param suspendCurrentQueueTimeMillis
     */
	public void setSuspendCurrentQueueTimeMillis(long suspendCurrentQueueTimeMillis) {
		this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
	}

    /**
     * <p>Returns the consume timeout.</p>
     * @return the get consume timeout
     */
	public long getConsumeTimeout() {
		return consumeTimeout;
	}

    /**
     * <p>Sets the consume timeout.</p>
     * @param consumeTimeout
     */
	public void setConsumeTimeout(long consumeTimeout) {
		this.consumeTimeout = consumeTimeout;
	}
	
    /**
     * <p>Returns the retry times when consume failed.</p>
     * @return the get retry times when consume failed
     */
	public int getRetryTimesWhenConsumeFailed() {
		return retryTimesWhenConsumeFailed;
	}

    /**
     * <p>Sets the retry times when consume failed.</p>
     * @param retryTimesWhenConsumeFailed
     */
	public void setRetryTimesWhenConsumeFailed(int retryTimesWhenConsumeFailed) {
		this.retryTimesWhenConsumeFailed = retryTimesWhenConsumeFailed;
	}
	
    /**
     * <p>Returns the delay level when next consume.</p>
     * @return the get delay level when next consume
     */
	public int getDelayLevelWhenNextConsume() {
		return delayLevelWhenNextConsume;
	}

    /**
     * <p>Sets the delay level when next consume.</p>
     * @param delayLevelWhenNextConsume
     */
	public void setDelayLevelWhenNextConsume(int delayLevelWhenNextConsume) {
		this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
	}

    /**
     * <p>Returns the delay start seconds.</p>
     * @return the get delay start seconds
     */
	public int getDelayStartSeconds() {
		return delayStartSeconds;
	}

    /**
     * <p>Sets the delay start seconds.</p>
     * @param delayStartSeconds
     */
	public void setDelayStartSeconds(int delayStartSeconds) {
		this.delayStartSeconds = delayStartSeconds;
	}
	
	
}
