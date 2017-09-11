package org.apache.rocketmq.spring.boot.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.boot.exception.MessageBuildException;

import com.alibaba.fastjson.JSONObject;

public class MessageBuilder implements Builder<Message> {

	private String topic;
	private String tags;
	private String keys;
	private byte[] body;

	public MessageBuilder topic(String topic) {
		this.topic = topic;
		return this;
	}
	
	public MessageBuilder tags(String tags) {
		this.tags = tags;
		return this;
	}
	
	public MessageBuilder keys(String keys) {
		this.keys = keys;
		return this;
	}
	
	public MessageBuilder body(String body) {
		this.body = body.getBytes();
		return this;
	}
	
	public MessageBuilder body(Object body) {
		this.body = JSONObject.toJSONString(body).getBytes();
		return this;
	}
	
	public MessageBuilder body(byte[] body) {
		this.body = body;
		return this;
	}
	
	@Override
	public Message build() {
		
		if (StringUtils.isEmpty(topic)) {
			throw new MessageBuildException("topic is empty");
		}
		if (StringUtils.isEmpty(tags)) {
			throw new MessageBuildException("tags is empty");
		}
		if (StringUtils.isEmpty(keys)) {
			throw new MessageBuildException("keys is empty");
		}
		if ( null == body ) {
			throw new MessageBuildException("body is null");
		}
		return new Message(topic, // topic
				tags, // tags
				keys, // key用于标识业务的唯一性； key 消息关键词，多个Key用KEY_SEPARATOR隔开（查询消息使用）
				body// body 二进制字节数组
		);
	}

}
