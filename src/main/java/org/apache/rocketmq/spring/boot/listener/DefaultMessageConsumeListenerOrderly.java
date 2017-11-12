package org.apache.rocketmq.spring.boot.listener;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.RocketmqConsumerProperties;
import org.apache.rocketmq.spring.boot.handler.MessageOrderlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultMessageConsumeListenerOrderly implements MessageListenerOrderly {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageConsumeListenerOrderly.class);

	/**
	 * 真正处理消息的实现对象
	 */
	@Autowired
	private MessageOrderlyHandler messageHandler;
	@Autowired
	private RocketmqConsumerProperties properties;

	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgExts, ConsumeOrderlyContext context) {
		// 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
		LOG.debug(Thread.currentThread().getName() + " Receive New Messages: " + msgExts.size());
		// 消费消息内容
		for (MessageExt msgExt : msgExts) {
			LOG.debug("Receive msg: {}", msgExt);
			try {
				long now = System.currentTimeMillis();
				messageHandler.handleMessage(msgExt, context);
				long costTime = System.currentTimeMillis() - now;
                LOG.info("Message （MsgID : {} ）Consumed.  cost: {} ms", msgExt.getMsgId(), costTime);
			} catch (Exception e) {
				LOG.warn("Consume message failed. messageExt:{}", msgExt, e);
                context.setSuspendCurrentQueueTimeMillis(properties.getSuspendCurrentQueueTimeMillis());
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
			}
		}
		// 如果没有return success，consumer会重复消费此信息，直到success。
		return ConsumeOrderlyStatus.SUCCESS;
	}

	public MessageOrderlyHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageOrderlyHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	public RocketmqConsumerProperties getProperties() {
		return properties;
	}

	public void setProperties(RocketmqConsumerProperties properties) {
		this.properties = properties;
	}
	
}
