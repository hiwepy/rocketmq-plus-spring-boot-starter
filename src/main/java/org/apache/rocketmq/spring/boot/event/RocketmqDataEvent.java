package org.apache.rocketmq.spring.boot.event;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.rocketmq.common.message.MessageExt;

@SuppressWarnings("serial")
public class RocketmqDataEvent implements Serializable{

	private MessageExt messageExt;
	private String topic;
	private String tag;
	private byte[] body;

	public RocketmqDataEvent() {
	}
	
	public RocketmqDataEvent(MessageExt msgExt) throws Exception {
		this.topic = msgExt.getTopic();
		this.tag = msgExt.getTags();
		this.body = msgExt.getBody();
		this.messageExt = msgExt;
	}
	
	public String getMsgBody() {
		try {
			return new String(this.body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public String getMsgBody(String code) {
		try {
			return new String(this.body, code);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public void setMessageExt(MessageExt messageExt) {
		this.messageExt = messageExt;
	}

	public MessageExt getMessageExt() {
		return messageExt;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
