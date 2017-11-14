package org.apache.rocketmq.spring.boot.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.RocketmqPushConsumerProperties;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.NestedMessageConcurrentlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;

public class DefaultMessageListenerConcurrently implements MessageListenerConcurrently, ApplicationContextAware, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageListenerConcurrently.class);

	@Autowired
	private RocketmqPushConsumerProperties properties;
	/**
	 * 真正处理消息的实现对象
	 */
	private MessageConcurrentlyHandler messageHandler;
	private ApplicationContext applicationContext;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		List<MessageConcurrentlyHandler> handlers = new ArrayList<MessageConcurrentlyHandler>();
		
		// 查找Spring上下文中注册的MessageConcurrentlyHandler接口实现
		Map<String, MessageConcurrentlyHandler> beansOfType = getApplicationContext().getBeansOfType(MessageConcurrentlyHandler.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, MessageConcurrentlyHandler>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, MessageConcurrentlyHandler> entry = ite.next();
				if (entry.getValue() instanceof NestedMessageConcurrentlyHandler ) {
					//跳过其他嵌套实现
					continue;
				}
				handlers.add(entry.getValue());
			}
		}
		
		messageHandler = new NestedMessageConcurrentlyHandler(handlers);
		
	}

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgExts, ConsumeConcurrentlyContext context) {

		// 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
		LOG.debug(Thread.currentThread().getName() + " Receive New Messages: " + msgExts.size());
		// 重试次数
		int retryTimes = properties.getRetryTimesWhenConsumeFailed();
		// 消费消息内容
		for (MessageExt msgExt : msgExts) {
			
			LOG.debug("Receive msg: {}", msgExt);
			
			Exception exception = null;
			
			try {
	
				boolean continueHandle = messageHandler.preHandle(msgExt, context);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Invoked preHandle method.  Continuing Handle?: [" + continueHandle + "]");
				}
				
				if (continueHandle) {
					
					long now = System.currentTimeMillis();
					messageHandler.handleMessage(msgExt, context);
					long costTime = System.currentTimeMillis() - now;
	                LOG.info("Message （MsgID : {} ）Consumed.  cost: {} ms", msgExt.getMsgId(), costTime);
	               
				}
				
				messageHandler.postHandle(msgExt, context);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Successfully invoked postHandle method");
				}
	
			} catch (Exception e) {
				
				exception = e;
				
				context.setDelayLevelWhenNextConsume(properties.getDelayLevelWhenNextConsume());
				if (msgExt.getReconsumeTimes() < retryTimes) {
					// TODO 消息消费失败，进行日志记录
					String error = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
					LOG.debug(String.format("Consume Error : %s , Message （MsgID : %s ） Reconsume.", error, msgExt.getMsgId()));
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
				
			} finally {
				cleanup(msgExt, context, exception);
			}
			
		}
		// 如果没有return success，consumer会重复消费此信息，直到success。
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
	protected void cleanup(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception existing) {
		Exception exception = existing;
		try {
			messageHandler.afterCompletion(msgExt, context, exception);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Successfully invoked afterCompletion method.");
			}
		} catch (Exception e) {
			if (exception == null) {
				exception = e;
			} else {
				LOG.debug("afterCompletion implementation threw an exception.  This will be ignored to "
						+ "allow the original source exception to be propagated.", e);
			}
		}
	}

	public MessageConcurrentlyHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageConcurrentlyHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	public RocketmqPushConsumerProperties getProperties() {
		return properties;
	}

	public void setProperties(RocketmqPushConsumerProperties properties) {
		this.properties = properties;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
