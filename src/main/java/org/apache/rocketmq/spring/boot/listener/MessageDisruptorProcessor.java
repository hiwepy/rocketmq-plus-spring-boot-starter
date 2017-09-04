package org.apache.rocketmq.spring.boot.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.disruptor.RocketmqDataEventTranslator;
import org.apache.rocketmq.spring.boot.event.RocketmqDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.lmax.disruptor.dsl.Disruptor;

public class MessageDisruptorProcessor implements MessageProcessor, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(MessageDisruptorProcessor.class);
	@Autowired
	private Disruptor<RocketmqDataEvent> disruptor;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		/** 
         * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从MetaQ服务器上注销自己 
         * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法 
         */  
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
            public void run() {  
            	disruptor.shutdown(); 
            }  
        }));
	}
	
	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			// 生产消息
			disruptor.publishEvent(new RocketmqDataEventTranslator(context), msgExt);
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}

}