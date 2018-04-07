/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.rocketmq.spring.boot.listener;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;

/**
 * 执行本地事务
 */
public class DefaultLocalTransactionExecuter implements LocalTransactionExecuter {
	
	private AtomicInteger transactionIndex = new AtomicInteger(1);

	@Override

	public LocalTransactionState executeLocalTransactionBranch(final Message msg, final Object arg) {
		
		int value = transactionIndex.getAndIncrement();
		if (value == 0) {
			throw new RuntimeException("Could not find db");
		}

		else if ((value % 5) == 0) {
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}

		else if ((value % 4) == 0) {
			return LocalTransactionState.COMMIT_MESSAGE;
		}
		
		return LocalTransactionState.UNKNOW;

	}

}
