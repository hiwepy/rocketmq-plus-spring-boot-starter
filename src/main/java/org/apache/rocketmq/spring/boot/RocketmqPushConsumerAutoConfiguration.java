package org.apache.rocketmq.spring.boot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueConsistentHash;
import org.apache.rocketmq.client.consumer.store.OffsetStore;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider;
import org.apache.rocketmq.spring.boot.config.SubscriptionProvider;
import org.apache.rocketmq.spring.boot.exception.RocketMQException;
import org.apache.rocketmq.spring.boot.hooks.MQPushConsumerShutdownHook;
import org.apache.rocketmq.spring.boot.listener.DefaultMessageListenerConcurrently;
import org.apache.rocketmq.spring.boot.listener.DefaultMessageListenerOrderly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

/**
 * Spring Boot auto-configuration for the RocketMQ <strong>push</strong> consumer.
 * <p>
 * Activated when {@code rocketmq.consume-passively.enabled=true}. Registers a
 * {@link DefaultMQPushConsumer} with default concurrently/orderly message
 * listeners, subscribes to the topics declared via a
 * {@link SubscriptionProvider} or the {@code subscription} property map, and
 * exposes a {@link RocketmqPushConsumerTemplate}. The consumer is started after
 * a configurable delay so that Spring event listeners are ready before messages
 * arrive.
 * </p>
 *
 * <h3>Configuration keys</h3>
 * <ul>
 *   <li>{@code rocketmq.consume-passively.enabled} — must be {@code true}</li>
 *   <li>{@code rocketmq.consume-passively.consumer-group} — consumer group (required)</li>
 *   <li>{@code rocketmq.consume-passively.namesrv-addr} — name server (required)</li>
 *   <li>{@code rocketmq.consume-passively.instance-name} — instance name (required)</li>
 *   <li>{@code rocketmq.consume-passively.consume-mode} — {@code CONCURRENTLY} or {@code ORDERLY}</li>
 *   <li>{@code rocketmq.consume-passively.selector-type} — {@code TAG} or {@code SQL92}</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({ DefaultMQPushConsumer.class })
@ConditionalOnProperty(prefix = RocketmqPushConsumerProperties.PREFIX, value = "enabled", havingValue = "true")
@AutoConfigureAfter(RocketmqPushEventHandlerAutoConfiguration.class)
@EnableConfigurationProperties({ RocketmqPushConsumerProperties.class })
public class RocketmqPushConsumerAutoConfiguration  {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqPushConsumerAutoConfiguration.class);
	
	@Bean
	@ConditionalOnMissingBean
    /**
     * <p>Message listener concurrently.</p>
     * @return the message listener concurrently
     */
	public MessageListenerConcurrently messageListenerConcurrently() {
		return new DefaultMessageListenerConcurrently();
	}
	
	@Bean
	@ConditionalOnMissingBean
    /**
     * <p>Message listener orderly.</p>
     * @return the message listener orderly
     */
	public MessageListenerOrderly messageListenerOrderly() {
		return new DefaultMessageListenerOrderly();
	}

	/**
	 * Queue allocation algorithm specifying how message queues are allocated to
	 * each consumer clients.
	 */
	@Bean
	@ConditionalOnMissingBean
    /**
     * <p>Allocate message queue strategy.</p>
     * @return the allocate message queue strategy
     */
	public AllocateMessageQueueStrategy allocateMessageQueueStrategy() {
		return new AllocateMessageQueueConsistentHash();
	}

	/*
	 * Initialise the push consumer from the bound properties.
	 */
	public void configure(DefaultMQPushConsumer consumer, RocketmqPushConsumerProperties properties) {
		
		consumer.resetClientConfig(properties);
		
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
		// Batch consume size; setting it introduces consumption latency.
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
			consumer.setMessageModel(MessageModel.CLUSTERING);
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
	
	@Bean
	@ConditionalOnMissingBean
    /**
     * <p>Default sub provider.</p>
     * @return the default sub provider
     */
	public SubscriptionProvider defaultSubProvider() {
		return new DefaultSubscriptionProvider();
	}
	
	/*
	 * Initialise the RocketMQ push consumer.
	 */
	@Bean
	@ConditionalOnMissingBean
	public DefaultMQPushConsumer pushConsumer(RocketmqPushConsumerProperties properties,
			@Autowired(required = false) OffsetStore offsetStore,
			@Autowired(required = false) SubscriptionProvider subProvider,
			MessageListenerOrderly messageListenerOrderly,
			MessageListenerConcurrently messageListenerConcurrently,
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

		try {

			/*
			 * One application should create a single Consumer and maintain it
			 * (e.g. as a singleton). The consumer group name must be unique.
			 */
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getConsumerGroup());

			consumer.setAllocateMessageQueueStrategy(allocateMessageQueueStrategy);

			// Optional server-side filtering via Java code.
			//String filterCode = MixAll.file2String("D:\\workspace\\rocketmq-quickstart\\src\\main\\java\\com\\zoo\\quickstart\\filter\\MessageFilterImpl.java");
			//consumer.subscribe("TopicFilter7", "com.zoo.quickstart.filter.MessageFilterImpl", filterCode);
			
			
			// Initialise consumer parameters.
			this.configure(consumer, properties);

			// consumer.setOffsetStore(offsetStore);
			/*
			 * Subscribe to the configured topics and selector expressions.
			 */
			Map<String /* topic */, String /* selectorExpress */> subscription = new HashMap<String, String>();
			if(subProvider != null) {
				Map<String /* topic */, String /* selectorExpress */> subs = subProvider.subscription();
				if(!CollectionUtils.isEmpty(subs) ){
					subscription.putAll(subs);
				}
			}
			if(!CollectionUtils.isEmpty(properties.getSubscription()) ){
				subscription.putAll(properties.getSubscription());
			}
			
			if(!CollectionUtils.isEmpty(subscription) ){
				
				Iterator<Entry<String, String>> ite = subscription.entrySet().iterator();
				while (ite.hasNext()) {
					Entry<String, String> entry = ite.next();
					/* 
					 * entry.getKey()   : topic name
					 * entry.getValue() : selector expression for the topic
					 */
					String topic = entry.getKey();
					String selectorExpress = entry.getValue();
					switch (properties.getSelectorType()) {
			            case TAG:{
			                consumer.subscribe(topic, selectorExpress);
						};break;
			            case SQL92:{
			                consumer.subscribe(topic, MessageSelector.bySql(selectorExpress));
			            };break;
			            default:{
			                throw new IllegalArgumentException("Property 'selectorType' was wrong.");
			            }
			        }
				}
				
			}

			/*
			 * Register the consume listener.
			 */
			switch (properties.getConsumeMode()) {
	            case ORDERLY:
	                consumer.setMessageListener(messageListenerOrderly);
	                break;
	            case CONCURRENTLY:
	                consumer.setMessageListener(messageListenerConcurrently);
	                break;
	            default:
	                throw new IllegalArgumentException("Property 'consumeMode' was wrong.");
			}
			
			/*
			 * Delay the start by a few seconds so Spring event listeners finish
			 * initialising; otherwise consuming a message and immediately
			 * publishing a message-arrived event could lose the event because
			 * its listener is not yet registered.
			 */
			Executors.newScheduledThreadPool(1).schedule(new Thread() {
    /**
     * <p>Run.</p>
     */
				public void run() {
					try {

						/*
						 * The consumer must be started once before use.
						 */
						consumer.start();

						LOG.info("RocketMQ MQPushConsumer Started ! groupName:[%s],namesrvAddr:[%s],instanceName:[%s].",
								properties.getConsumerGroup(), properties.getNamesrvAddr(), properties.getInstanceName());
						
						/**
						 * On application exit call shutdown to release
						 * resources, close network connections and unregister
						 * from the broker. It is recommended to call shutdown
						 * from the JVM shutdown hook (e.g. when running inside
						 * JBoss/Tomcat).
						 */
						Runtime.getRuntime().addShutdownHook(new MQPushConsumerShutdownHook(consumer));

					} catch (Exception e) {
						LOG.error(String.format("RocketMQ MQPushConsumer Start failure ：%s", e.getMessage(), e));
					}
				}
			}, properties.getDelayStartSeconds(), TimeUnit.SECONDS);

			return consumer;

		} catch (Exception e) {
			throw new RocketMQException(e);
		}
	}
	
	@Bean
    /**
     * <p>Rocketmq consumer template.</p>
     * @param consumer
     * @return the rocketmq consumer template
     */
	public RocketmqPushConsumerTemplate rocketmqConsumerTemplate(MQPushConsumer consumer) throws MQClientException {
		return new RocketmqPushConsumerTemplate(consumer);
	}

}
