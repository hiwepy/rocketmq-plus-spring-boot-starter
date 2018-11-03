package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;

/**
 */
public interface MessageConcurrentlyHandler {
	
	boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;

	void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;
    
	void postHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;
    
    void afterCompletion(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception ex) throws Exception;
    
}