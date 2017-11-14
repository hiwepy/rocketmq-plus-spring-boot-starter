package org.apache.rocketmq.spring.boot;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;
import org.apache.rocketmq.client.consumer.MessageQueueListener;
import org.apache.rocketmq.client.consumer.PullTaskCallback;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueConsistentHash;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.boot.annotation.RocketmqPullTopic;
import org.apache.rocketmq.spring.boot.exception.RocketMQException;
import org.apache.rocketmq.spring.boot.hooks.MQPullConsumerScheduleShutdownHook;
import org.apache.rocketmq.spring.boot.hooks.MQPullConsumerShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;

@Configuration
@ConditionalOnClass({ DefaultMQPushConsumer.class })
@ConditionalOnProperty(prefix = RocketmqPullConsumerProperties.PREFIX, value = "enabled", havingValue = "true")
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 20)
@EnableConfigurationProperties({ RocketmqPullConsumerProperties.class })
public class RocketmqPullConsumerAutoConfiguration  implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqPullConsumerAutoConfiguration.class);
	private ApplicationContext applicationContext;
	
	/**
	 * Queue allocation algorithm specifying how message queues are allocated to
	 * each consumer clients.
	 */
	@Bean
	@ConditionalOnMissingBean
	public AllocateMessageQueueStrategy allocateMessageQueueStrategy() {
		return new AllocateMessageQueueConsistentHash();
	}

	/**
	 * 初始化消息消费者
	 * 
	 * @param consumer
	 * @param properties
	 */
	public void configure(DefaultMQPullConsumer consumer, RocketmqPullConsumerProperties properties) {
		
		consumer.resetClientConfig(properties);
		
		consumer.setBrokerSuspendMaxTimeMillis(properties.getBrokerSuspendMaxTimeMillis());
		consumer.setClientCallbackExecutorThreads(properties.getClientCallbackExecutorThreads());
		consumer.setClientIP(properties.getClientIP());
		consumer.setConsumerGroup(properties.getConsumerGroup());
		consumer.setConsumerPullTimeoutMillis(properties.getConsumerPullTimeoutMillis());
		consumer.setConsumerTimeoutMillisWhenSuspend(properties.getConsumerTimeoutMillisWhenSuspend());
		consumer.setHeartbeatBrokerInterval(properties.getHeartbeatBrokerInterval());
		consumer.setInstanceName(properties.getInstanceName());
		consumer.setMaxReconsumeTimes(properties.getMaxReconsumeTimes());
		consumer.setNamesrvAddr(properties.getNamesrvAddr());
		try {
			consumer.setMessageModel(MessageModel.valueOf(properties.getMessageModel()));
		} catch (Exception e) {
			consumer.setMessageModel(MessageModel.CLUSTERING);
		}
		//consumer.setOffsetStore(offsetStore);
		consumer.setPersistConsumerOffsetInterval(properties.getPersistConsumerOffsetInterval());
		consumer.setPollNameServerInterval(properties.getPollNameServerInterval());
		consumer.setRegisterTopics(properties.getRegisterTopics());
		consumer.setUnitMode(properties.isUnitMode());
		consumer.setUnitName(properties.getUnitName());
		consumer.setVipChannelEnabled(properties.isVipChannelEnabled());
		
	}
	
	@Bean
	@ConditionalOnMissingBean
	public DefaultMQPullConsumer pullConsumer(RocketmqPullConsumerProperties properties,
			AllocateMessageQueueStrategy allocateMessageQueueStrategy) throws MQClientException {
		

		if (StringUtils.isEmpty(properties.getConsumerGroup())) {
			throw new RocketMQException("consumerGroup is empty");
		}
		if (StringUtils.isEmpty(properties.getNamesrvAddr())) {
			throw new RocketMQException("nameServerAddr is empty");
		}
		
		DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(properties.getConsumerGroup());
		
		// 初始化参数
		this.configure(consumer, properties);
					
		consumer.setAllocateMessageQueueStrategy(allocateMessageQueueStrategy);
		
		// 查找Spring上下文中注册的MessageQueueListener接口实现
		Map<String, MessageQueueListener> beansOfType = getApplicationContext().getBeansOfType(MessageQueueListener.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, MessageQueueListener>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, MessageQueueListener> entry = ite.next();
				//查找该实现上的注解
				RocketmqPullTopic annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), RocketmqPullTopic.class);
				if(annotationType == null) {
					// 注解为空，则跳过该实现，并打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", RocketmqPullTopic.class, entry.getValue().getClass(), entry.getKey());
					continue;
				}
				consumer.registerMessageQueueListener(annotationType.value(), entry.getValue());
			}
		}
		
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

					LOG.info("RocketMQ MQPullConsumer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
							properties.getConsumerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());
					
					/**
					 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从RocketMQ服务器上注销自己
					 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
					 */
					Runtime.getRuntime().addShutdownHook(new MQPullConsumerShutdownHook(consumer));

				} catch (Exception e) {
					LOG.error(String.format("RocketMQ MQPushConsumer Start failure ：%s", e.getMessage(), e));
				}
			}
		}, properties.getDelayStartSeconds(), TimeUnit.SECONDS);
		
		return consumer;
	} 
	
	@Bean
	@ConditionalOnProperty(prefix = RocketmqPullConsumerProperties.PREFIX, name = "schedulable", havingValue = "true")
	public MQPullConsumerScheduleService schedulePullConsumer(RocketmqPullConsumerProperties properties) throws MQClientException {

		if (StringUtils.isEmpty(properties.getConsumerGroup())) {
			throw new RocketMQException("consumerGroup is empty");
		}
		if (StringUtils.isEmpty(properties.getNamesrvAddr())) {
			throw new RocketMQException("nameServerAddr is empty");
		}
		
		MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService(properties.getConsumerGroup());

		DefaultMQPullConsumer consumer = scheduleService.getDefaultMQPullConsumer();
		// 初始化参数
		this.configure(consumer, properties);
		
		try {
			scheduleService.setMessageModel(MessageModel.valueOf(properties.getMessageModel()));
		} catch (Exception e) {
			scheduleService.setMessageModel(MessageModel.CLUSTERING);
		}
		
		scheduleService.setPullThreadNums(properties.getPullThreadNums());
		
		// 查找Spring上下文中注册的PullTaskCallback接口实现
		Map<String, PullTaskCallback> beansOfType = getApplicationContext().getBeansOfType(PullTaskCallback.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, PullTaskCallback>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, PullTaskCallback> entry = ite.next();
				//查找该实现上的注解
				RocketmqPullTopic annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), RocketmqPullTopic.class);
				if(annotationType == null) {
					// 注解为空，则跳过该实现，并打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", RocketmqPullTopic.class, entry.getValue().getClass(), entry.getKey());
					continue;
				}
				scheduleService.registerPullTaskCallback(annotationType.value(), entry.getValue());
			}
		}
		
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
					scheduleService.start();

					LOG.info("RocketMQ MQPullConsumerScheduleService Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
							properties.getConsumerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());
					
					/**
					 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从RocketMQ服务器上注销自己
					 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
					 */
					Runtime.getRuntime().addShutdownHook(new MQPullConsumerScheduleShutdownHook(scheduleService));

				} catch (Exception e) {
					LOG.error(String.format("RocketMQ MQPushConsumer Start failure ：%s", e.getMessage(), e));
				}
			}
		}, properties.getDelayStartSeconds(), TimeUnit.SECONDS);
		
		return scheduleService;
	}
	
	@Bean
	public RocketmqPullConsumerTemplate rocketmqConsumerTemplate(MQPullConsumer consumer) throws MQClientException {
		return new RocketmqPullConsumerTemplate(consumer);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
