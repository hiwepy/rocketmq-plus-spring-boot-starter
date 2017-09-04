package org.apache.rocketmq.spring.boot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.rocketmq.spring.boot.config.DisruptorConfig;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataEventFactory;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqEventHandler;
import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest
public class RingBufferTest {
	
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
	@ConditionalOnClass({ RingBuffer.class })
	protected RingBuffer<RocketmqDataEvent> ringBuffer(RocketmqProperties properties, WaitStrategy waitStrategy,
			EventFactory<RocketmqDataEvent> eventFactory,
			@Autowired(required = false) RocketmqEventHandler eventHandler) {

		DisruptorConfig config = properties.getDisruptor();

		// 创建线程池
		ExecutorService executor = Executors.newFixedThreadPool(config.getRingThreadNumbers());
		/*
		 * 第一个参数叫EventFactory，从名字上理解就是“事件工厂”，其实它的职责就是产生数据填充RingBuffer的区块。
		 * 第二个参数是RingBuffer的大小，它必须是2的指数倍 目的是为了将求模运算转为&运算提高效率
		 * 第三个参数是RingBuffer的生产都在没有可用区块的时候(可能是消费者（或者说是事件处理器） 太慢了)的等待策略
		 */
		RingBuffer<RocketmqDataEvent> ringBuffer = null;
		if (config.isMultiProducer()) {
			// RingBuffer.createMultiProducer创建一个多生产者的RingBuffer
			ringBuffer = RingBuffer.createMultiProducer(eventFactory, config.getRingBufferSize(), waitStrategy);
		} else {
			// RingBuffer.createSingleProducer创建一个单生产者的RingBuffer
			ringBuffer = RingBuffer.createSingleProducer(eventFactory, config.getRingBufferSize(), waitStrategy);
		}
		
		//单个处理器
		if (null != eventHandler) {
			// 创建SequenceBarrier
			SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
			// 创建消息处理器
			BatchEventProcessor<RocketmqDataEvent> transProcessor = new BatchEventProcessor<RocketmqDataEvent>(
					ringBuffer, sequenceBarrier, eventHandler);
			// 这一部的目的是让RingBuffer根据消费者的状态 如果只有一个消费者的情况可以省略
			ringBuffer.addGatingSequences(transProcessor.getSequence());
			// 把消息处理器提交到线程池
			executor.submit(transProcessor);
		}
		
		return ringBuffer;
	}
	
	

}
