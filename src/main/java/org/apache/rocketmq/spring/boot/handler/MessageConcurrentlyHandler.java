package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * Lifecycle contract for concurrent message handling, modelled after the
 * classic pre-handle / handle / post-handle / after-completion pattern.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface MessageConcurrentlyHandler {

	/**
	 * Pre-handle hook invoked before the message is processed.
	 *
	 * @param msgExt  the received message
	 * @param context the concurrent consume context
	 * @return {@code true} to continue processing, {@code false} to abort
	 * @throws Exception if the pre-handle fails
	 */
	boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;

	/**
	 * Handles the message.
	 *
	 * @param msgExt  the received message
	 * @param context the concurrent consume context
	 * @throws Exception if handling fails
	 */
	void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;

	/**
	 * Post-handle hook invoked after successful handling.
	 *
	 * @param msgExt  the received message
	 * @param context the concurrent consume context
	 * @throws Exception if the post-handle fails
	 */
	void postHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception;

	/**
	 * Completion hook invoked after processing regardless of success.
	 *
	 * @param msgExt  the received message
	 * @param context the concurrent consume context
	 * @param ex      any exception thrown during handling, or {@code null}
	 * @throws Exception if the completion fails
	 */
	void afterCompletion(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception ex) throws Exception;

}