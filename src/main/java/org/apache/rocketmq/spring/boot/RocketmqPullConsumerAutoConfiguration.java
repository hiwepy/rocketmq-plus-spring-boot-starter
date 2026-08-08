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
import org.apache.rocketmq.spring.boot.annotation.RocketmqPullCallback;
import org.apache.rocketmq.spring.boot.annotation.RocketmqPullConsumer;
import org.apache.rocketmq.spring.boot.exception.RocketMQException;
import org.apache.rocketmq.spring.boot.hooks.MQPullConsumerScheduleShutdownHook;
import org.apache.rocketmq.spring.boot.hooks.MQPullConsumerShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

/**
 * Spring Boot auto-configuration for the RocketMQ <strong>pull</strong> consumer.
 * <p>
 * Activated when {@code rocketmq.consume-actively.enabled=true}. Registers a
 * {@link DefaultMQPullConsumer} (and optionally an
 * {@link MQPullConsumerScheduleService} when {@code schedulable=true}), wires
 * {@link MessageQueueListener} and {@link PullTaskCallback} beans annotated
 * with {@link RocketmqPullConsumer} / {@link RocketmqPullCallback}, and exposes
 * a {@link RocketmqPullConsumerTemplate}. The consumer is started after a
 * configurable delay so that Spring event listeners are ready before messages
 * arrive.
 * </p>
 *
 * <h3>Configuration keys</h3>
 * <ul>
 *   <li>{@code rocketmq.consume-actively.enabled} — must be {@code true}</li>
 *   <li>{@code rocketmq.consume-actively.schedulable} — use the scheduled pull service</li>
 *   <li>{@code rocketmq.consume-actively.consumer-group} — consumer group (required)</li>
 *   <li>{@code rocketmq.consume-actively.namesrv-addr} — name server (required)</li>
 * </ul>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({ DefaultMQPushConsumer.class })
@ConditionalOnProperty(prefix = RocketmqPullConsumerProperties.PREFIX, value = "enabled", havingValue = "true")
@AutoConfigureAfter(RocketmqPushEventHandlerAutoConfiguration.class)
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
	 * Configures the supplied pull consumer from the bound properties.
	 *
	 * @param consumer    the consumer to configure
	 * @param properties  the pull consumer properties
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
		
		// Initialise consumer parameters.
		this.configure(consumer, properties);
					
		consumer.setAllocateMessageQueueStrategy(allocateMessageQueueStrategy);
		
		// Look up MessageQueueListener beans registered in the Spring context.
		Map<String, MessageQueueListener> beansOfType = getApplicationContext().getBeansOfType(MessageQueueListener.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, MessageQueueListener>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, MessageQueueListener> entry = ite.next();
				// Resolve the @RocketmqPullConsumer annotation on the bean.
				RocketmqPullConsumer annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), RocketmqPullConsumer.class);
				if(annotationType == null) {
					// No annotation: skip and log an error.
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", RocketmqPullConsumer.class, entry.getValue().getClass(), entry.getKey());
					continue;
				}
				
				
				consumer.registerMessageQueueListener(annotationType.topic(), entry.getValue());
			}
		}
		
		/*
		 * Delay the start by a few seconds so Spring event listeners finish
		 * initialising; otherwise consuming a message and immediately publishing
		 * a message-arrived event could lose the event because its listener is
		 * not yet registered.
		 */
		Executors.newScheduledThreadPool(1).schedule(new Thread() {
			public void run() {
				try {

					/*
					 * The consumer must be started once before use.
					 */
					consumer.start();

					LOG.info("RocketMQ MQPullConsumer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
							properties.getConsumerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());
					
					/**
					 * On application exit call shutdown to release resources,
					 * close network connections and unregister from the broker.
					 * It is recommended to call shutdown from the JVM shutdown
					 * hook (e.g. when running inside JBoss/Tomcat).
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
		// Initialise consumer parameters.
		this.configure(consumer, properties);
		
		try {
			scheduleService.setMessageModel(MessageModel.valueOf(properties.getMessageModel()));
		} catch (Exception e) {
			scheduleService.setMessageModel(MessageModel.CLUSTERING);
		}
		
		scheduleService.setPullThreadNums(properties.getPullThreadNums());
		
		// Look up PullTaskCallback beans registered in the Spring context.
		Map<String, PullTaskCallback> beansOfType = getApplicationContext().getBeansOfType(PullTaskCallback.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, PullTaskCallback>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, PullTaskCallback> entry = ite.next();
				// Resolve the @RocketmqPullCallback annotation on the bean.
				RocketmqPullCallback annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), RocketmqPullCallback.class);
				if(annotationType == null) {
					// No annotation: skip and log an error.
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", RocketmqPullCallback.class, entry.getValue().getClass(), entry.getKey());
					continue;
				}
				scheduleService.registerPullTaskCallback(annotationType.topic(), entry.getValue());
			}
		}
		
		/*
		 * Delay the start by a few seconds so Spring event listeners finish
		 * initialising; otherwise consuming a message and immediately publishing
		 * a message-arrived event could lose the event because its listener is
		 * not yet registered.
		 */
		Executors.newScheduledThreadPool(1).schedule(new Thread() {
			public void run() {
				try {

					/*
					 * The schedule service must be started once before use.
					 */
					scheduleService.start();

					LOG.info("RocketMQ MQPullConsumerScheduleService Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
							properties.getConsumerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());
					
					/**
					 * On application exit call shutdown to release resources,
					 * close network connections and unregister from the broker.
					 * It is recommended to call shutdown from the JVM shutdown
					 * hook (e.g. when running inside JBoss/Tomcat).
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
