/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
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
package org.apache.rocketmq.spring.boot.event;

import java.io.UnsupportedEncodingException;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.util.StringUtils;

import com.lmax.disruptor.spring.boot.event.DisruptorEvent;


/**
 * Disruptor-backed event wrapping a received RocketMQ {@link MessageExt},
 * carrying its topic, tag and body and deriving an Ant-style route expression
 * ({@code /topic/tags/keys}) for handler-chain resolution.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class RocketmqDisruptorEvent extends DisruptorEvent {

	/** The raw RocketMQ message. */
	private MessageExt messageExt;
	/** The message topic. */
	private String topic;
	/** The message tag. */
	private String tag;
	/** The raw message body bytes. */
	private byte[] body;

	/**
	 * @param source the event source (typically the message or a producer)
	 */
	public RocketmqDisruptorEvent(Object source) {
		super(source);
	}
	
	/**
	 * Returns the route expression, deriving it from the wrapped message when
	 * not explicitly set.
	 *
	 * @return the Ant-style {@code /topic/tags/keys} route expression
	 */
	@Override
	public String getRouteExpression() {
		String expression = super.getRouteExpression();
		if(StringUtils.isEmpty(expression)){
			return this.buildRouteExpression(messageExt);
		}
		return expression;
	}
	
	/**
	 * Builds the {@code /topic/tags/keys} route expression for the message.
	 *
	 * @param msgExt the received message
	 * @return the route expression string
	 */
	private String buildRouteExpression(MessageExt msgExt) {
		return new StringBuilder("/").append(msgExt.getTopic()).append("/").append(msgExt.getTags()).append("/")
				.append(msgExt.getKeys()).toString();
	}

	/**
	 * @return the message body decoded as UTF-8, or {@code null} on failure
	 */
	public String getMsgBody() {
		try {
			return new String(this.body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * @param code the charset name to decode the body with
	 * @return the message body decoded with the given charset, or {@code null} on failure
	 */
	public String getMsgBody(String code) {
		try {
			return new String(this.body, code);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/** @return the raw RocketMQ message */
	public MessageExt getMessageExt() {
		return messageExt;
	}

	/** @param messageExt the raw RocketMQ message */
	public void setMessageExt(MessageExt messageExt) {
		this.messageExt = messageExt;
	}

	/** @return the message topic */
	public String getTopic() {
		return topic;
	}

	/** @param topic the message topic */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/** @return the message tag */
	public String getTag() {
		return tag;
	}

	/** @param tag the message tag */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/** @return the raw message body bytes */
	public byte[] getBody() {
		return body;
	}

	/** @param body the raw message body bytes */
	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
