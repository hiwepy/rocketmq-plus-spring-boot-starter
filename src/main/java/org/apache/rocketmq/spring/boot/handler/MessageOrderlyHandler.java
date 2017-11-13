package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;

/**
 */
public interface MessageOrderlyHandler {
	
	
	boolean preHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;
	
	/**
	 * 
	 * @description	：  处理消息的接口
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @date 		：2017年11月13日 上午10:08:50
	 * @param msgExt 消息对象
     * @param context 上下文
     * @return 是否处理完成
     * @throws Exception 处理异常
	 */
	void handleMessage(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;
    
	void postHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;
    
    void afterCompletion(MessageExt msgExt, ConsumeOrderlyContext context, Exception ex) throws Exception;
    
}