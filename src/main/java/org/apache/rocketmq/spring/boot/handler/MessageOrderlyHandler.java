package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;

/**
 */
public interface MessageOrderlyHandler {
	
	
	boolean preHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;
	
	void handleMessage(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;
    
	void postHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;
    
    void afterCompletion(MessageExt msgExt, ConsumeOrderlyContext context, Exception ex) throws Exception;
    
}