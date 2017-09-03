package org.apache.rocketmq.spring.boot.listener;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.message.MessageExt;

public class DefaultTransactionCheckListener implements TransactionCheckListener {
	
	@Override
	public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
		return null;
	}

}
