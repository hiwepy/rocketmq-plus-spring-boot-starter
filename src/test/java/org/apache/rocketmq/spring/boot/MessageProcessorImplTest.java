package org.apache.rocketmq.spring.boot;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.listener.MessageProcessor;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessorImplTest implements MessageProcessor {
    

	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		System.out.println("receive : " + msgExt.toString());
		return true;
	}
    
}