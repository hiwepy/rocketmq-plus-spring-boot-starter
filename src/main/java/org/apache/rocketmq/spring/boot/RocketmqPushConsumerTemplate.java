package org.apache.rocketmq.spring.boot;

import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByRandom;
import org.apache.rocketmq.spring.boot.enums.ConsumeMode;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.def.PathMatchingHandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Helper template for the RocketMQ push consumer, exposing the underlying
 * {@link MQPushConsumer} together with convenience methods for subscribing to
 * topics and binding {@link EventHandler} instances to the handler-chain
 * registry.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class RocketmqPushConsumerTemplate {

	/** Hash-based message queue selector for orderly messaging. */
	public final MessageQueueSelector HASH_SELECTOR = new SelectMessageQueueByHash();
	/** Random message queue selector. */
	public final MessageQueueSelector RANDOOM_SELECTOR = new SelectMessageQueueByRandom();
	/** Separator used when joining multiple tag expressions. */
	public final String SELECTOR_EXPRESSS_EPARATOR = " || ";
	
	@Autowired
	private RocketmqEventMessageOrderlyHandler messageOrderlyHandler;
	@Autowired
	private RocketmqEventMessageConcurrentlyHandler messageConcurrentlyHandler;
	@Autowired
	private RocketmqPushConsumerProperties pushConsumerProperties;
	
	private MQPushConsumer consumer;

	public RocketmqPushConsumerTemplate(MQPushConsumer consumer) {
		this.consumer = consumer;
	}
	
	public void subscribe(String topic, String tags, String handlerName, EventHandler<RocketmqEvent> handler) throws MQClientException {

		PathMatchingHandlerChainResolver chainResolver = getChainResolver();
		if(chainResolver == null) {
			return;
		}
		HandlerChainManager<RocketmqEvent> chainManager = chainResolver.getHandlerChainManager();

		// Build a unique handler name.
		String chainDefinition = handlerName;
		// Register a new handler instance.
		chainManager.addHandler(chainDefinition, handler);
		
		// Split the tag expression.
		String[] tagArr = StringUtils.tokenizeToStringArray(tags);
		for (String tag : tagArr) {
			// Build the dispatch rule chain: topic/tags/keys
			String rule = new StringBuilder().append("/").append(topic).append("/").append(tag).append("/*").toString();
			chainManager.createChain(rule, chainDefinition);
		}
		
		// Subscribe the consumer to the topic.
		String selectorExpress = StringUtils.join(tagArr, SELECTOR_EXPRESSS_EPARATOR);
		switch (pushConsumerProperties.getSelectorType()) {
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
	
	public void unsubscribe(String topic, String tags, String handlerName) {
		
		PathMatchingHandlerChainResolver chainResolver = getChainResolver();
		if(chainResolver == null) {
			return;
		}
		
		HandlerChainManager<RocketmqEvent> chainManager = chainResolver.getHandlerChainManager();
		
		chainManager.getHandlers().remove(handlerName);
		
		// Split the tag expression.
		String[] tagArr = StringUtils.tokenizeToStringArray(tags, ",");
		for (String tag : tagArr) {
			// topic/tags/keys
			String rule = new StringBuilder().append(topic).append("/").append(tag).append("/*").toString();
			chainManager.getHandlerChains().remove(rule);
		}
		
		// Unsubscribe the consumer from the topic.
		consumer.unsubscribe(topic);
		
	}

	public void registerMessageListener(final MessageListenerConcurrently messageListener){
		consumer.registerMessageListener(messageListener);
	}

	public void registerMessageListener(final MessageListenerOrderly messageListener){
		consumer.registerMessageListener(messageListener);
	}
	
	protected PathMatchingHandlerChainResolver getChainResolver() {
		PathMatchingHandlerChainResolver chainResolver = null;
		if( pushConsumerProperties != null && pushConsumerProperties.isEnabled() ) {
			// Select the handler based on the configured consume mode.
			if (ConsumeMode.ORDERLY.compareTo(pushConsumerProperties.getConsumeMode()) == 0) {
				chainResolver = (PathMatchingHandlerChainResolver) getMessageOrderlyHandler().getHandlerChainResolver();
			}else {
				chainResolver = (PathMatchingHandlerChainResolver) getMessageConcurrentlyHandler().getHandlerChainResolver();
			}
		}
		return chainResolver;
	}

	public RocketmqEventMessageOrderlyHandler getMessageOrderlyHandler() {
		return messageOrderlyHandler;
	}

	public void setMessageOrderlyHandler(RocketmqEventMessageOrderlyHandler messageOrderlyHandler) {
		this.messageOrderlyHandler = messageOrderlyHandler;
	}

	public RocketmqEventMessageConcurrentlyHandler getMessageConcurrentlyHandler() {
		return messageConcurrentlyHandler;
	}

	public void setMessageConcurrentlyHandler(RocketmqEventMessageConcurrentlyHandler messageConcurrentlyHandler) {
		this.messageConcurrentlyHandler = messageConcurrentlyHandler;
	}

	public MQPushConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(MQPushConsumer consumer) {
		this.consumer = consumer;
	}
	
}
