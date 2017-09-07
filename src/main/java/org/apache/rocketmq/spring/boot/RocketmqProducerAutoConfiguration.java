package org.apache.rocketmq.spring.boot;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.spring.boot.exception.RocketMQException;
import org.apache.rocketmq.spring.boot.hooks.MQProducerShutdownHook;
import org.apache.rocketmq.spring.boot.listener.DefaultTransactionCheckListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnClass({ DefaultMQProducer.class })
@ConditionalOnProperty(name = RocketmqProducerProperties.PREFIX, matchIfMissing = true)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 10)
@EnableConfigurationProperties({ RocketmqProducerProperties.class })
public class RocketmqProducerAutoConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqProducerAutoConfiguration.class);
	
	@Bean
	@ConditionalOnProperty(prefix = RocketmqProducerProperties.PREFIX, value = "transaction", havingValue = "true", matchIfMissing = true)
	public TransactionCheckListener transactionCheckListener() {
		return new DefaultTransactionCheckListener();
	}

	/**
	 * 初始化消息生产者
	 * 
	 * @param producer
	 * @param properties
	 */
	public void configure(DefaultMQProducer producer, RocketmqProducerProperties properties) {
		producer.setClientCallbackExecutorThreads(properties.getClientCallbackExecutorThreads());
		producer.setClientIP(properties.getClientIP());
		producer.setCompressMsgBodyOverHowmuch(properties.getCompressMsgBodyOverHowmuch());
		producer.setCreateTopicKey(properties.getCreateTopicKey());
		producer.setDefaultTopicQueueNums(properties.getDefaultTopicQueueNums());
		producer.setHeartbeatBrokerInterval(properties.getHeartbeatBrokerInterval());
		producer.setInstanceName(properties.getInstanceName());
		producer.setLatencyMax(properties.getLatencyMax());
		producer.setMaxMessageSize(properties.getMaxMessageSize());
		producer.setNamesrvAddr(properties.getNamesrvAddr());
		producer.setNotAvailableDuration(properties.getNotAvailableDuration());
		producer.setPersistConsumerOffsetInterval(properties.getPersistConsumerOffsetInterval());
		producer.setPollNameServerInterval(properties.getPollNameServerInterval());
		producer.setProducerGroup(properties.getProducerGroup());
		producer.setRetryAnotherBrokerWhenNotStoreOK(properties.isRetryAnotherBrokerWhenNotStoreOK());
		producer.setRetryTimesWhenSendAsyncFailed(properties.getRetryTimesWhenSendAsyncFailed());
		producer.setRetryTimesWhenSendFailed(properties.getRetryTimesWhenSendFailed());
		producer.setSendLatencyFaultEnable(properties.isSendLatencyFaultEnable());
		producer.setSendMessageWithVIPChannel(properties.isVipChannelEnabled());
		producer.setSendMsgTimeout(properties.getSendMsgTimeout());
		producer.setUnitMode(properties.isUnitMode());
		producer.setUnitName(properties.getUnitName());
		producer.setVipChannelEnabled(properties.isVipChannelEnabled());
	}

	/**
	 * 初始化向rocketmq发送普通消息的生产者
	 */
	@Bean
	@ConditionalOnProperty(prefix = RocketmqProducerProperties.PREFIX, value = "producerGroup")
	public DefaultMQProducer defaultProducer(RocketmqProducerProperties properties,
			TransactionCheckListener transactionCheckListener) throws MQClientException {

		if (StringUtils.isEmpty(properties.getProducerGroup())) {
			throw new RocketMQException("producerGroup is empty");
		}
		if (StringUtils.isEmpty(properties.getNamesrvAddr())) {
			throw new RocketMQException("nameServerAddr is empty");
		}
		if (StringUtils.isEmpty(properties.getInstanceName())) {
			throw new RocketMQException("instanceName is empty");
		}

		/*
		 * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ProducerGroupName需要由应用来保证唯一<br>
		 * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
		 * 因为服务器会回查这个Group下的任意一个Producer
		 */

		// 是否需要事物
		if (properties.isTransaction()) {
			try {
				/*
				 * 初始化向rocketmq发送事务消息的生产者
				 */
				TransactionMQProducer producer = new TransactionMQProducer(properties.getProducerGroup());

				// 初始化参数
				this.configure(producer, properties);

				// 事务回查最小并发数
				producer.setCheckThreadPoolMinSize(properties.getCheckThreadPoolMinSize());
				// 事务回查最大并发数
				producer.setCheckThreadPoolMaxSize(properties.getCheckThreadPoolMaxSize());
				// 队列数
				producer.setCheckRequestHoldMax(properties.getCheckRequestHoldMax());
				// TODO 由于社区版本的服务器阉割调了消息回查的功能，所以这个地方没有意义
				producer.setTransactionCheckListener(transactionCheckListener);

				/*
				 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br> 注意：切记不可以在每次发送消息时，都调用start方法
				 */
				producer.start();

				LOG.info("RocketMQ TransactionMQProducer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
						properties.getProducerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());
				/**
				 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从RocketMQ服务器上注销自己
				 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
				 */
				Runtime.getRuntime().addShutdownHook(new MQProducerShutdownHook(producer));
				
				return producer;

			} catch (Exception e) {
				LOG.error(String.format("Producer is error {}", e.getMessage(), e));
				throw new RocketMQException(e);
			}

		} else {

			try {

				// 创建生产者对象
				DefaultMQProducer producer = new DefaultMQProducer(properties.getProducerGroup());

				// 初始化参数
				this.configure(producer, properties);

				/*
				 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br> 注意：切记不可以在每次发送消息时，都调用start方法
				 */
				producer.start();

				LOG.info("RocketMQ MQProducer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
						properties.getProducerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());

				/**
				 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从RocketMQ服务器上注销自己
				 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
				 */
				Runtime.getRuntime().addShutdownHook(new MQProducerShutdownHook(producer));

				return producer;
			} catch (Exception e) {
				LOG.error(String.format("RocketMQ MQProducer Start failure ：%s", e.getMessage(), e));
				throw new RocketMQException(e);
			}
		}

	}
 
	@Bean
	public RocketmqProducerTemplate rocketmqProducerTemplate(DefaultMQProducer producer) throws MQClientException {
		return new RocketmqProducerTemplate(producer);
	}
	
}
