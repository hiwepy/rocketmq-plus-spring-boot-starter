/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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
package org.apache.rocketmq.spring.boot.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.MixAll;

public class ProducerConfig extends ClientConfig {

	/**
	 * Producer group conceptually aggregates all producer instances of exactly
	 * same role, which is particularly important when transactional messages
	 * are involved.
	 * </p>
	 *
	 * For non-transactional messages, it does not matter as long as it's unique
	 * per process.
	 * </p>
	 *
	 * See {@linktourl http://rocketmq.incubator.apache.org/docs/core-concept/}
	 * for more discussion.
	 */
	private String producerGroup;

	/**
	 * Just for testing or demo program
	 */
	private String createTopicKey = MixAll.DEFAULT_TOPIC;

	/**
	 * Number of queues to create per default topic.
	 */
	private volatile int defaultTopicQueueNums = 4;

	/**
	 * Timeout for sending messages.
	 */
	private int sendMsgTimeout = 3000;
	
	private boolean sendLatencyFaultEnable = false;
	
	/**
	 * Compress message body threshold, namely, message body larger than 4k will
	 * be compressed on default.
	 */
	private int compressMsgBodyOverHowmuch = 1024 * 4;

	/**
	 * Maximum number of retry to perform internally before claiming sending
	 * failure in synchronous mode.
	 * </p>
	 *
	 * This may potentially cause message duplication which is up to application
	 * developers to resolve.
	 */
	private int retryTimesWhenSendFailed = 2;

	/**
	 * Maximum number of retry to perform internally before claiming sending
	 * failure in asynchronous mode.
	 * </p>
	 *
	 * This may potentially cause message duplication which is up to application
	 * developers to resolve.
	 */
	private int retryTimesWhenSendAsyncFailed = 2;

	/**
	 * Indicate whether to retry another broker on sending failure internally.
	 */
	private boolean retryAnotherBrokerWhenNotStoreOK = false;

	/**
	 * Maximum allowed message size in bytes.
	 */
	private int maxMessageSize = 1024 * 1024 * 4; // 4M
	
	private long[] latencyMax;
	
	private long[] notAvailableDuration;
	
	/** 是否启用事物 **/
	private boolean transaction = false;
	/** 事物检查监听 **/
	private TransactionCheckListener transactionCheckListener;
	/** 事务回查最小并发数 **/
	private int checkThreadPoolMinSize = 1;
	/** 事务回查最大并发数 **/
	private int checkThreadPoolMaxSize = 1;
	/** 队列数 **/
	private int checkRequestHoldMax = 2000;

	public String getProducerGroup() {
		return StringUtils.isEmpty(producerGroup) ? "ProducerGroup" : producerGroup;
	}

	public void setProducerGroup(String producerGroup) {
		this.producerGroup = producerGroup;
	}

	public String getCreateTopicKey() {
		return createTopicKey;
	}

	public void setCreateTopicKey(String createTopicKey) {
		this.createTopicKey = createTopicKey;
	}

	public int getDefaultTopicQueueNums() {
		return defaultTopicQueueNums;
	}

	public void setDefaultTopicQueueNums(int defaultTopicQueueNums) {
		this.defaultTopicQueueNums = defaultTopicQueueNums;
	}

	public int getSendMsgTimeout() {
		return sendMsgTimeout;
	}

	public void setSendMsgTimeout(int sendMsgTimeout) {
		this.sendMsgTimeout = sendMsgTimeout;
	}

	public boolean isSendLatencyFaultEnable() {
		return sendLatencyFaultEnable;
	}

	public void setSendLatencyFaultEnable(boolean sendLatencyFaultEnable) {
		this.sendLatencyFaultEnable = sendLatencyFaultEnable;
	}

	public int getCompressMsgBodyOverHowmuch() {
		return compressMsgBodyOverHowmuch;
	}

	public void setCompressMsgBodyOverHowmuch(int compressMsgBodyOverHowmuch) {
		this.compressMsgBodyOverHowmuch = compressMsgBodyOverHowmuch;
	}

	public int getRetryTimesWhenSendFailed() {
		return retryTimesWhenSendFailed;
	}

	public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
		this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
	}

	public int getRetryTimesWhenSendAsyncFailed() {
		return retryTimesWhenSendAsyncFailed;
	}

	public void setRetryTimesWhenSendAsyncFailed(int retryTimesWhenSendAsyncFailed) {
		this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
	}

	public boolean isRetryAnotherBrokerWhenNotStoreOK() {
		return retryAnotherBrokerWhenNotStoreOK;
	}

	public void setRetryAnotherBrokerWhenNotStoreOK(boolean retryAnotherBrokerWhenNotStoreOK) {
		this.retryAnotherBrokerWhenNotStoreOK = retryAnotherBrokerWhenNotStoreOK;
	}

	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public long[] getLatencyMax() {
		return latencyMax;
	}

	public void setLatencyMax(long[] latencyMax) {
		this.latencyMax = latencyMax;
	}

	public long[] getNotAvailableDuration() {
		return notAvailableDuration;
	}

	public void setNotAvailableDuration(long[] notAvailableDuration) {
		this.notAvailableDuration = notAvailableDuration;
	}
	
	public boolean isTransaction() {
		return transaction;
	}

	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}

	public TransactionCheckListener getTransactionCheckListener() {
		return transactionCheckListener;
	}

	public void setTransactionCheckListener(TransactionCheckListener transactionCheckListener) {
		this.transactionCheckListener = transactionCheckListener;
	}

	public int getCheckThreadPoolMinSize() {
		return checkThreadPoolMinSize;
	}

	public void setCheckThreadPoolMinSize(int checkThreadPoolMinSize) {
		this.checkThreadPoolMinSize = checkThreadPoolMinSize;
	}

	public int getCheckThreadPoolMaxSize() {
		return checkThreadPoolMaxSize;
	}

	public void setCheckThreadPoolMaxSize(int checkThreadPoolMaxSize) {
		this.checkThreadPoolMaxSize = checkThreadPoolMaxSize;
	}

	public int getCheckRequestHoldMax() {
		return checkRequestHoldMax;
	}

	public void setCheckRequestHoldMax(int checkRequestHoldMax) {
		this.checkRequestHoldMax = checkRequestHoldMax;
	}
	
}

