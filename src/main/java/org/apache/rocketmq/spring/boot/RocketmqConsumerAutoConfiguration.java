package org.apache.rocketmq.spring.boot;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueConsistentHash;
import org.apache.rocketmq.client.consumer.store.OffsetStore;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.boot.exception.RocketMQException;
import org.apache.rocketmq.spring.boot.hooks.MQPushConsumerShutdownHook;
import org.apache.rocketmq.spring.boot.listener.DefaultMessageConsumeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

@Configuration
@ConditionalOnClass({ DefaultMQPushConsumer.class })
@ConditionalOnProperty(prefix = RocketmqConsumerProperties.PREFIX, value = "enabled", havingValue = "true")
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 20)
@EnableConfigurationProperties({ RocketmqConsumerProperties.class })
public class RocketmqConsumerAutoConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqConsumerAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = RocketmqConsumerProperties.PREFIX, value = "consumerGroup")
	public MessageListenerConcurrently messageListener() {
		return new DefaultMessageConsumeListener();
	}

	/*
	 * @Bean
	 * 
	 * @ConditionalOnMissingBean
	 * 
	 * @ConditionalOnProperty(prefix = RocketmqProperties.CONSUMER_PREFIX , value =
	 * "consumerGroup") public OffsetStore offsetStore() { return null; }
	 */

	/**
	 * Queue allocation algorithm specifying how message queues are allocated to
	 * each consumer clients.
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = RocketmqConsumerProperties.PREFIX, value = "consumerGroup")
	public AllocateMessageQueueStrategy allocateMessageQueueStrategy() {
		return new AllocateMessageQueueConsistentHash();
	}

	/**
	 * 初始化消息消费者
	 * 
	 * @param consumer
	 * @param properties
	 */
	public void configure(DefaultMQPushConsumer consumer, RocketmqConsumerProperties properties) {
		consumer.setAdjustThreadPoolNumsThreshold(properties.getAdjustThreadPoolNumsThreshold());
		consumer.setClientCallbackExecutorThreads(properties.getClientCallbackExecutorThreads());
		consumer.setClientIP(properties.getClientIP());
		consumer.setConsumeConcurrentlyMaxSpan(properties.getConsumeConcurrentlyMaxSpan());
		try {
			consumer.setConsumeFromWhere(ConsumeFromWhere.valueOf(properties.getConsumeFromWhere()));
		} catch (Exception e) {
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		}
		consumer.setConsumeMessageBatchMaxSize(properties.getConsumeMessageBatchMaxSize());
		consumer.setConsumerGroup(properties.getConsumerGroup());
		// 设置批量消费个数;设置后会出现数据消费延时
		consumer.setConsumeThreadMax(properties.getConsumeThreadMax());
		consumer.setConsumeThreadMin(properties.getConsumeThreadMin());
		consumer.setConsumeTimeout(properties.getConsumeTimeout());
		consumer.setConsumeTimestamp(properties.getConsumeTimestamp());
		consumer.setHeartbeatBrokerInterval(properties.getHeartbeatBrokerInterval());
		consumer.setInstanceName(properties.getInstanceName());
		consumer.setMaxReconsumeTimes(properties.getMaxReconsumeTimes());
		try {
			consumer.setMessageModel(MessageModel.valueOf(properties.getMessageModel()));
		} catch (Exception e) {
		}
		consumer.setNamesrvAddr(properties.getNamesrvAddr());
		consumer.setPersistConsumerOffsetInterval(properties.getPersistConsumerOffsetInterval());
		consumer.setPollNameServerInterval(properties.getPollNameServerInterval());
		consumer.setPostSubscriptionWhenPull(properties.isPostSubscriptionWhenPull());
		consumer.setPullBatchSize(properties.getPullBatchSize());
		consumer.setPullInterval(properties.getPullInterval());
		consumer.setPullThresholdForQueue(properties.getPullThresholdForQueue());
		consumer.setSuspendCurrentQueueTimeMillis(properties.getSuspendCurrentQueueTimeMillis());
		consumer.setUnitMode(properties.isUnitMode());
		consumer.setUnitName(properties.getUnitName());
		consumer.setVipChannelEnabled(properties.isVipChannelEnabled());
	}

	/**
	 * 初始化rocketmq消息监听方式的消费者
	 */
	@Bean
	@ConditionalOnProperty(prefix = RocketmqConsumerProperties.PREFIX, value = "consumerGroup")
	public DefaultMQPushConsumer pushConsumer(RocketmqConsumerProperties properties,
			MessageListenerConcurrently messageListener, 
			@Autowired(required = false) OffsetStore offsetStore,
			AllocateMessageQueueStrategy allocateMessageQueueStrategy) throws MQClientException {


		if (StringUtils.isEmpty(properties.getConsumerGroup())) {
			throw new RocketMQException("consumerGroup is empty");
		}
		if (StringUtils.isEmpty(properties.getNamesrvAddr())) {
			throw new RocketMQException("nameServerAddr is empty");
		}
		if (StringUtils.isEmpty(properties.getInstanceName())) {
			throw new RocketMQException("instanceName is empty");
		}
		if (CollectionUtils.isEmpty(properties.getSubscription())) {
			throw new RocketMQException("subscription is empty");
		}

		try {

			/*
			 * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br> 注意：ConsumerGroupName需要由应用来保证唯一
			 */
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getConsumerGroup());

			consumer.setAllocateMessageQueueStrategy(allocateMessageQueueStrategy);

			// 初始化参数
			this.configure(consumer, properties);

			// consumer.setOffsetStore(offsetStore);
			/*
			 * 订阅指定topic下tags
			 */
			if(!CollectionUtils.isEmpty(properties.getSubscription())){
				
				Iterator<Entry<String, String>> ite = properties.getSubscription().entrySet().iterator();
				while (ite.hasNext()) {
					Entry<String, String> entry = ite.next();
					/* 
					 * entry.getKey() 	： topic名称 
					 * entry.getValue() : 根据实际情况设置消息的tag 
					 */
					consumer.subscribe(entry.getKey(), entry.getValue());
				}
				
			}

			/*
			 * 注册消费监听
			 */
			consumer.registerMessageListener(messageListener);
			
			/*
			 * 延迟5秒再启动，主要是等待spring事件监听相关程序初始化完成，否则，回出现对RocketMQ的消息进行消费后立即发布消息到达的事件，
			 * 然而此事件的监听程序还未初始化，从而造成消息的丢失
			 */
			Executors.newScheduledThreadPool(1).schedule(new Thread() {
				public void run() {
					try {

						/*
						 * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
						 */
						consumer.start();

						LOG.info("RocketMQ MQPushConsumer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
								properties.getConsumerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());
						
						/**
						 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从RocketMQ服务器上注销自己
						 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
						 */
						Runtime.getRuntime().addShutdownHook(new MQPushConsumerShutdownHook(consumer));

					} catch (Exception e) {
						LOG.error(String.format("RocketMQ MQPushConsumer Start failure ：%s", e.getMessage(), e));
					}
				}
			}, 5, TimeUnit.SECONDS);

			return consumer;

		} catch (Exception e) {
			throw new RocketMQException(e);
		}
	}
	
	@Bean
	public RocketmqConsumerTemplate rocketmqConsumerTemplate(MQPushConsumer consumer) throws MQClientException {
		return new RocketmqConsumerTemplate(consumer);
	}
	

}
