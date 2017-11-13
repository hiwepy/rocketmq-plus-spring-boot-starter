package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.AbstractRouteableMessageHandler;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketmqEventMessageConcurrentlyHandler extends AbstractRouteableMessageHandler<RocketmqEvent> implements MessageConcurrentlyHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqEventMessageConcurrentlyHandler.class);
	
	public RocketmqEventMessageConcurrentlyHandler(HandlerChainResolver<RocketmqEvent> filterChainResolver) {
		super(filterChainResolver);
	}
	
	@Override
	public boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		return true;
	}
	
	@Override
	public void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		//构造原始链对象
		HandlerChain<RocketmqEvent>	originalChain = new ProxiedHandlerChain();
		//执行事件处理链
		this.doHandler(new RocketmqEvent(msgExt), originalChain);
	}

	@Override
	public void postHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		
	}

	@Override
	public void afterCompletion(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception ex) throws Exception {
		if(ex != null) {
			LOG.warn("Consume message failed. messageExt:{}", msgExt, ex);
		}
	}
	
}