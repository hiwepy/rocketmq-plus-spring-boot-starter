package org.apache.rocketmq.spring.boot;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.spring.boot.config.ConsumerConfig;
import org.apache.rocketmq.spring.boot.config.ProducerConfig;
import org.apache.rocketmq.spring.boot.config.Subscribe;
import org.apache.rocketmq.spring.boot.exception.RocketMQException;
import org.apache.rocketmq.spring.boot.listener.DefaultMessageConsumeListener;
import org.apache.rocketmq.spring.boot.listener.DefaultTransactionCheckListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ DefaultMQProducer.class, DefaultMQPushConsumer.class })
//@ConditionalOnProperty(prefix = RocketmqProperties.PREFIX, matchIfMissing = false)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 10)
@EnableConfigurationProperties({ RocketmqProperties.class })
public class RocketmqAutoConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = RocketmqProperties.PRODUCER_PREFIX + ".transaction", value = "true")
	public TransactionCheckListener transactionCheckListener() {
		return new DefaultTransactionCheckListener();
	}
	
	/**
	 * 初始化消息生产者
	 * 
	 * @param producer
	 * @param config
	 */
	public void configure(DefaultMQProducer producer, ProducerConfig config) {
		producer.setClientCallbackExecutorThreads(config.getClientCallbackExecutorThreads());
		producer.setClientIP(config.getClientIP());
		producer.setCompressMsgBodyOverHowmuch(config.getCompressMsgBodyOverHowmuch());
		producer.setCreateTopicKey(config.getCreateTopicKey());
		producer.setDefaultTopicQueueNums(config.getDefaultTopicQueueNums());
		producer.setHeartbeatBrokerInterval(config.getHeartbeatBrokerInterval());
		producer.setInstanceName(config.getInstanceName());
		producer.setLatencyMax(config.getLatencyMax());
		producer.setMaxMessageSize(config.getMaxMessageSize());
		producer.setNamesrvAddr(config.getNamesrvAddr());
		producer.setNotAvailableDuration(config.getNotAvailableDuration());
		producer.setPersistConsumerOffsetInterval(config.getPersistConsumerOffsetInterval());
		producer.setPollNameServerInterval(config.getPollNameServerInterval());
		producer.setProducerGroup(config.getProducerGroup());
		producer.setRetryAnotherBrokerWhenNotStoreOK(config.isRetryAnotherBrokerWhenNotStoreOK());
		producer.setRetryTimesWhenSendAsyncFailed(config.getRetryTimesWhenSendAsyncFailed());
		producer.setRetryTimesWhenSendFailed(config.getRetryTimesWhenSendFailed());
		producer.setSendLatencyFaultEnable(config.isSendLatencyFaultEnable());
		producer.setSendMessageWithVIPChannel(config.isVipChannelEnabled());
		producer.setSendMsgTimeout(config.getSendMsgTimeout());
		producer.setUnitMode(config.isUnitMode());
		producer.setUnitName(config.getUnitName());
		producer.setVipChannelEnabled(config.isVipChannelEnabled());
	}
	
	/**
	 * 初始化向rocketmq发送普通消息的生产者
	 */
	@Bean
	@ConditionalOnProperty(prefix = RocketmqProperties.PRODUCER_PREFIX + ".producerGroup")
	public DefaultMQProducer defaultProducer(RocketmqProperties properties,
			TransactionCheckListener transactionCheckListener) throws MQClientException {

		ProducerConfig config = properties.getProducer();

		if (StringUtils.isEmpty(config.getProducerGroup())) {
			throw new RocketMQException("producerGroup is empty");
		}
		if (StringUtils.isEmpty(config.getNamesrvAddr())) {
			throw new RocketMQException("nameServerAddr is empty");
		}
		if (StringUtils.isEmpty(config.getInstanceName())) {
			throw new RocketMQException("instanceName is empty");
		}

		/*
		 * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ProducerGroupName需要由应用来保证唯一<br>
		 * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
		 * 因为服务器会回查这个Group下的任意一个Producer
		 */

		// 是否需要事物
		if (config.isTransaction()) {
			try {
				/*
				 * 初始化向rocketmq发送事务消息的生产者
				 */
				TransactionMQProducer producer = new TransactionMQProducer(config.getProducerGroup());

				// 初始化参数
				this.configure(producer, config);

				// 事务回查最小并发数
				producer.setCheckThreadPoolMinSize(config.getCheckThreadPoolMinSize());
				// 事务回查最大并发数
				producer.setCheckThreadPoolMaxSize(config.getCheckThreadPoolMaxSize());
				// 队列数
				producer.setCheckRequestHoldMax(config.getCheckRequestHoldMax());
				// TODO 由于社区版本的服务器阉割调了消息回查的功能，所以这个地方没有意义
				producer.setTransactionCheckListener(transactionCheckListener);

				/*
				 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br> 注意：切记不可以在每次发送消息时，都调用start方法
				 */
				producer.start();

				LOG.info("RocketMQ TransactionMQProducer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
						config.getProducerGroup(), config.getNamesrvAddr(), config.getInstanceName());

				return producer;

			} catch (Exception e) {
				LOG.error(String.format("Producer is error {}", e.getMessage(), e));
				throw new RocketMQException(e);
			}

		} else {

			try {

				// 创建生产者对象
				DefaultMQProducer producer = new DefaultMQProducer(config.getProducerGroup());

				// 初始化参数
				this.configure(producer, config);

				/*
				 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br> 注意：切记不可以在每次发送消息时，都调用start方法
				 */
				producer.start();

				LOG.info("RocketMQ MQProducer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
						config.getProducerGroup(), config.getNamesrvAddr(), config.getInstanceName());
				
				/** 
		         * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从RocketMQ服务器上注销自己 
		         * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法 
		         */  
		        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
		            public void run() {  
		            	producer.shutdown(); 
		            }  
		        })); 
		        
				return producer;
			} catch (Exception e) {
				LOG.error(String.format("RocketMQ MQProducer Start failure ：%s", e.getMessage(), e));
				throw new RocketMQException(e);
			}
		}

	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = RocketmqProperties.CONSUMER_PREFIX + ".consumerGroup")
	public MessageListenerConcurrently messageListener() {
		return new DefaultMessageConsumeListener();
	}
	
	/**
	 * 初始化rocketmq消息监听方式的消费者
	 */
	@Bean
	@ConditionalOnProperty(prefix = RocketmqProperties.CONSUMER_PREFIX + ".consumerGroup")
	public DefaultMQPushConsumer pushConsumer(RocketmqProperties properties,
			MessageListenerConcurrently messageListener) throws MQClientException {

		// 消费者配置
		ConsumerConfig config = properties.getConsumer();

		if (StringUtils.isEmpty(config.getConsumerGroup())) {
			throw new RocketMQException("consumerGroup is empty");
		}
		if (StringUtils.isEmpty(config.getNamesrvAddr())) {
			throw new RocketMQException("nameServerAddr is empty");
		}
		if (StringUtils.isEmpty(config.getInstanceName())) {
			throw new RocketMQException("instanceName is empty");
		}
		if (CollectionUtils.isEmpty(config.getSubscribe())) {
			throw new RocketMQException("subscribe is empty");
		}

		try {

			/*
			 * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br> 注意：ConsumerGroupName需要由应用来保证唯一
			 */
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(config.getConsumerGroup());
			try {
				consumer.setConsumeFromWhere(ConsumeFromWhere.valueOf(config.getConsumeFromWhere()));
			} catch (Exception e) {
				consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
			}

			/*
			 * 订阅指定topic下tags
			 */
			List<Subscribe> subscribeList = properties.getConsumer().getSubscribe();
			for (Subscribe sunscribe : subscribeList) {
				consumer.subscribe(sunscribe.getTopic(), sunscribe.getTags());
			}

			/*
			 * 注册消费监听
			 */
			consumer.registerMessageListener(messageListener);
			
			/** 
	         * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从RocketMQ服务器上注销自己 
	         * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法 
	         */  
	        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
	            public void run() {  
	            	consumer.shutdown(); 
	            }  
	        }));  
	        
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
								config.getConsumerGroup(), config.getNamesrvAddr(), config.getInstanceName());

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
	
}
