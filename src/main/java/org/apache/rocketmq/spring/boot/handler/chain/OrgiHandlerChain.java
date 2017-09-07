package org.apache.rocketmq.spring.boot.handler.chain;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

public class OrgiHandlerChain implements HandlerChain<RocketmqEvent> {

	@Override
	public void doHandler(RocketmqEvent event) throws Exception {
		
		long threadId = Thread.currentThread().getId();
		System.out.println(String.format("Thread Id %s Topic %s Tag %s into db ....", threadId , event.getTopic() , event.getTag() ));
		
	}

}
