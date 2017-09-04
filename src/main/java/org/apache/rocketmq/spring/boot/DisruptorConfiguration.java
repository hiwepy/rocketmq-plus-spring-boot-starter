package org.apache.rocketmq.spring.boot;

import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.boot.config.DisruptorConfig;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataEventFactory;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataEventThreadFactory;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqEventHandler;
import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;
import org.apache.rocketmq.spring.boot.listener.MessageDisruptorProcessor;
import org.apache.rocketmq.spring.boot.listener.MessageProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

@Configuration
@ConditionalOnClass({ DefaultMQProducer.class, DefaultMQPushConsumer.class, Disruptor.class })
@ConditionalOnProperty(prefix = RocketmqProperties.DISRUPTOR_PREFIX)
@EnableConfigurationProperties({ RocketmqProperties.class })
@SuppressWarnings("unchecked")
public class DisruptorConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public WaitStrategy waitStrategy() {
		return new YieldingWaitStrategy();
	}

	@Bean
	@ConditionalOnMissingBean
	public EventFactory<RocketmqDataEvent> eventFactory() {
		return new RocketmqDataEventFactory();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ThreadFactory threadFactory() {
		return new RocketmqDataEventThreadFactory();
	}
	
	@Bean
	@ConditionalOnClass({ Disruptor.class })
	protected Disruptor<RocketmqDataEvent> disruptor(RocketmqProperties properties, WaitStrategy waitStrategy,
			EventFactory<RocketmqDataEvent> eventFactory, ThreadFactory threadFactory) {

		DisruptorConfig config = properties.getDisruptor();

		Disruptor<RocketmqDataEvent> disruptor = null;
		if (config.isMultiProducer()) {
			disruptor = new Disruptor<RocketmqDataEvent>(eventFactory,
					config.getRingBufferSize(), threadFactory, ProducerType.MULTI, waitStrategy);
		} else {
			disruptor = new Disruptor<RocketmqDataEvent>(eventFactory,
					config.getRingBufferSize(), threadFactory, ProducerType.SINGLE, waitStrategy);
		}
		
		// 使用disruptor创建消费者组
		EventHandlerGroup<RocketmqDataEvent> handlerGroup = null;
		//多个处理器
		if(ArrayUtils.isNotEmpty(config.getPreHandlers())) {
			handlerGroup = disruptor.handleEventsWith(config.getPreHandlers());
			//后置处理;可以在完成前面的逻辑后执行新的逻辑
			if(ArrayUtils.isNotEmpty(config.getPostHandlers())) {
				// 完成前置事件处理之后执行后置事件处理
				handlerGroup.then(config.getPostHandlers());
			}
		} 
		//单个处理器
		else if (null != config.getEventHandler()) {
			RocketmqEventHandler eventHandler = config.getEventHandler();
			handlerGroup = disruptor.handleEventsWith(eventHandler);
			//后置处理;可以在完成前面的逻辑后执行新的逻辑
			if(!ObjectUtils.isEmpty(eventHandler.getNext())) {
				// 完成前置事件处理之后执行后置事件处理
				handlerGroup.then(eventHandler.getNext());
			}
		}
	    
		// 启动
		disruptor.start();
		
		return disruptor;
		
	}

	@Bean
	@ConditionalOnBean({ Disruptor.class })
	@ConditionalOnProperty(prefix = RocketmqProperties.DISRUPTOR_PREFIX)
	public MessageProcessor messageProcessor() {
		return new MessageDisruptorProcessor();
	}

}
