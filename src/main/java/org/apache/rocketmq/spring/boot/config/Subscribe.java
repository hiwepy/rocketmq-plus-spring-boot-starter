package org.apache.rocketmq.spring.boot.config;

public class Subscribe {

	/** topic名称 */
	private String topic;

	/** 根据实际情况设置消息的tag */
	private String tags;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

}
