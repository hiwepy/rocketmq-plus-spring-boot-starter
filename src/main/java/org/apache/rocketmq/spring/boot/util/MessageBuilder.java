package org.apache.rocketmq.spring.boot.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.boot.exception.MessageBuildException;

import com.alibaba.fastjson.JSONObject;

/**
 * Fluent builder for constructing RocketMQ {@link Message} instances.
 * <p>
 * Topic, tags and keys are required; the body may be supplied as a String, a
 * serialisable Object (JSON-encoded) or a raw byte array.
 * </p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class MessageBuilder implements Builder<Message> {

	/** Message topic. */
	private String topic;
	/** Message tags. */
	private String tags;
	/** Message business keys (unique identifier, query keywords). */
	private String keys;
	/** Raw message body bytes. */
	private byte[] body;

	/**
	 * @param topic the message topic
	 * @return this builder for chaining
	 */
	public MessageBuilder topic(String topic) {
		this.topic = topic;
		return this;
	}

	/**
	 * @param tags the message tags
	 * @return this builder for chaining
	 */
	public MessageBuilder tags(String tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * @param keys the message business keys
	 * @return this builder for chaining
	 */
	public MessageBuilder keys(String keys) {
		this.keys = keys;
		return this;
	}

	/**
	 * Sets the body from the given String using the platform default charset.
	 *
	 * @param body the message body as a String
	 * @return this builder for chaining
	 */
	public MessageBuilder body(String body) {
		this.body = body.getBytes();
		return this;
	}

	/**
	 * Sets the body by JSON-encoding the given object.
	 *
	 * @param body the message body as an object
	 * @return this builder for chaining
	 */
	public MessageBuilder body(Object body) {
		this.body = JSONObject.toJSONString(body).getBytes();
		return this;
	}

	/**
	 * @param body the raw message body bytes
	 * @return this builder for chaining
	 */
	public MessageBuilder body(byte[] body) {
		this.body = body;
		return this;
	}

	/**
	 * Builds the {@link Message}, validating that topic, tags, keys and body
	 * are all set.
	 *
	 * @return the constructed RocketMQ message
	 * @throws MessageBuildException if any required field is missing
	 */
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
				keys, // key: business unique identifier / query keyword, multiple keys separated by KEY_SEPARATOR
				body  // body: binary byte array
		);
	}

}
