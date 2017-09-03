package org.apache.rocketmq.spring.boot.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;

/**
 */
public interface MessageProcessor {
	
    /**
     * 处理消息的接口
     * @param msgExts
     * @return
     */
    public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;
    
}