package org.apache.rocketmq.spring.boot;

import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MessageQueueListener;

public class RocketmqPullConsumerTemplate {

	// Java缓存
	//private static final Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();

	protected MQPullConsumer consumer;

	public RocketmqPullConsumerTemplate(MQPullConsumer consumer) {
		this.consumer = consumer;
	}

	public void registerMessageListener(final String topic, final MessageQueueListener listener) {
		consumer.registerMessageQueueListener(topic, listener);
	}

	/*public void fetchSubscribeMessageQueues(final String topic) throws MQClientException {
		// 拉取订阅主题的队列，默认队列大小是4
		Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
		for (MessageQueue mq : mqs) {
			System.out.println("Consume from the queue: " + mq);
			SINGLE_MQ: 
			while (true) {
				try {
					//阻塞的拉去消息，中止时间默认20s  
					PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
					List<MessageExt> list = pullResult.getMsgFoundList();
					if (list != null && list.size() < 100) {
						for (MessageExt msg : list) {
							//System.out.println(SerializableInterface.deserialize(msg.getBody()));
						}
					}
					System.out.println(pullResult.getNextBeginOffset());
					putMessageQueueOffset(mq, pullResult.getNextBeginOffset());

					switch (pullResult.getPullStatus()) {
					case FOUND:
						// TODO
						break;
					case NO_MATCHED_MSG:
						break;
					case NO_NEW_MSG:
						break SINGLE_MQ;
					case OFFSET_ILLEGAL:
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void putMessageQueueOffset(MessageQueue mq, long offset) {
		offseTable.put(mq, offset);
	}

	private static long getMessageQueueOffset(MessageQueue mq) {
		Long offset = offseTable.get(mq);
		if (offset != null) {
			System.out.println(offset);
			return offset;
		}
		return 0;
	}*/

	public MQPullConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(MQPullConsumer consumer) {
		this.consumer = consumer;
	}

}
