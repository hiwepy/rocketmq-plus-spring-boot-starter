/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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


@SuppressWarnings("serial")
public class RocketmqDisruptorEvent extends DisruptorEvent {

	private MessageExt messageExt;
	private String topic;
	private String tag;
	private byte[] body;

	public RocketmqDisruptorEvent(Object source) {
		super(source);
	}
	
	@Override
	public String getRouteExpression() {
		String expression = super.getRouteExpression();
		if(StringUtils.isEmpty(expression)){
			return this.buildRouteExpression(messageExt);
		}
		return expression;
	}
	
	private String buildRouteExpression(MessageExt msgExt) {
		return new StringBuilder("/").append(msgExt.getTopic()).append("/").append(msgExt.getTags()).append("/")
				.append(msgExt.getKeys()).toString();
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

	public MessageExt getMessageExt() {
		return messageExt;
	}

	public void setMessageExt(MessageExt messageExt) {
		this.messageExt = messageExt;
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
