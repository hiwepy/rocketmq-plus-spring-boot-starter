package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;

/**
 */
public interface MessageHandler {
	
    /**
     * 
     * @description	： 处理消息的接口
     * @author 		： 万大龙（743）
     * @date 		：2017年9月5日 上午11:09:13
     * @param msgExt 消息对象
     * @param context 上下文
     * @return 是否处理完成
     * @throws Exception 处理异常
     */
    public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;
    
}