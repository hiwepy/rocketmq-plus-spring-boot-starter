package org.apache.rocketmq.spring.boot.listener;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

public class DefaultTransactionCheckListener implements TransactionListener {
	
	private AtomicInteger transactionIndex = new AtomicInteger(0);
	
	@Override
	public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		return null;
	}
	
	@Override
	public LocalTransactionState checkLocalTransaction(MessageExt msg) {
		System.out.println("server checking TrMsg " + msg.toString());

        int value = transactionIndex.getAndIncrement();
        if ((value % 6) == 0) {
            throw new RuntimeException("Could not find db");
        } else if ((value % 5) == 0) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else if ((value % 4) == 0) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }

        return LocalTransactionState.UNKNOW;

		// 进行业务检查
		//return LocalTransactionState.COMMIT_MESSAGE;
	}

}
