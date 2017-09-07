package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.AbstractRouteableMessageHandler;
import org.apache.rocketmq.spring.boot.handler.MessageHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver;

@Component
public class RocketmqEventMessageHandler extends AbstractRouteableMessageHandler<RocketmqEvent> implements MessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqEventMessageHandler.class);
	
	public RocketmqEventMessageHandler(HandlerChainResolver<RocketmqEvent> filterChainResolver) {
		super(filterChainResolver);
	}
	
	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			
			HandlerChain<RocketmqEvent> originalChain = new HandlerChain<RocketmqEvent>() {
				
				@Override
				public void doHandler(RocketmqEvent event) throws Exception {
					
				}
				
			};
			//执行事件处理链
			this.doHandler(new RocketmqEvent(msgExt), originalChain);
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}
	
}