package org.apache.rocketmq.spring.boot.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.RocketmqPushConsumerProperties;
import org.apache.rocketmq.spring.boot.handler.MessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.NestedMessageOrderlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;

public class DefaultMessageListenerOrderly implements MessageListenerOrderly, ApplicationContextAware, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageListenerOrderly.class);
	
	@Autowired
	private RocketmqPushConsumerProperties properties;
	/**
	 * 真正处理消息的实现对象
	 */
	private MessageOrderlyHandler messageHandler;
	private ApplicationContext applicationContext;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		List<MessageOrderlyHandler> handlers = new ArrayList<MessageOrderlyHandler>();
		
		// 查找Spring上下文中注册的MessageOrderlyHandler接口实现
		Map<String, MessageOrderlyHandler> beansOfType = getApplicationContext().getBeansOfType(MessageOrderlyHandler.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, MessageOrderlyHandler>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, MessageOrderlyHandler> entry = ite.next();
				if (entry.getValue() instanceof NestedMessageOrderlyHandler ) {
					//跳过其他嵌套实现
					continue;
				}
				handlers.add(entry.getValue());
			}
		}
		
		messageHandler = new NestedMessageOrderlyHandler(handlers);
		
	}
	
	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgExts, ConsumeOrderlyContext context) {
		// 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
		LOG.debug(Thread.currentThread().getName() + " Receive New Messages: " + msgExts.size());
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
                context.setSuspendCurrentQueueTimeMillis(properties.getSuspendCurrentQueueTimeMillis());
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
			} finally {
				cleanup(msgExt, context, exception);
			}
			
		}
		// 如果没有return success，consumer会重复消费此信息，直到success。
		return ConsumeOrderlyStatus.SUCCESS;
	}
	
	protected void cleanup(MessageExt msgExt, ConsumeOrderlyContext context, Exception existing) {
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

	public MessageOrderlyHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageOrderlyHandler messageHandler) {
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
