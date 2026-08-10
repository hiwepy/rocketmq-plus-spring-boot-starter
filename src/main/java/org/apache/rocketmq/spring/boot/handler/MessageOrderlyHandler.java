package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * Lifecycle contract for orderly message handling, modelled after the
 * classic pre-handle / handle / post-handle / after-completion pattern.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface MessageOrderlyHandler {


	/**
	 * Pre-handle hook invoked before the message is processed.
	 *
	 * @param msgExt  the received message
	 * @param context the orderly consume context
	 * @return {@code true} to continue processing, {@code false} to abort
	 * @throws Exception if the pre-handle fails
	 */
	boolean preHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;

	/**
	 * Handles the message.
	 *
	 * @param msgExt  the received message
	 * @param context the orderly consume context
	 * @throws Exception if handling fails
	 */
	void handleMessage(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;

	/**
	 * Post-handle hook invoked after successful handling.
	 *
	 * @param msgExt  the received message
	 * @param context the orderly consume context
	 * @throws Exception if the post-handle fails
	 */
	void postHandle(MessageExt msgExt, ConsumeOrderlyContext context) throws Exception;

	/**
	 * Completion hook invoked after processing regardless of success.
	 *
	 * @param msgExt  the received message
	 * @param context the orderly consume context
	 * @param ex      any exception thrown during handling, or {@code null}
	 * @throws Exception if the completion fails
	 */
	void afterCompletion(MessageExt msgExt, ConsumeOrderlyContext context, Exception ex) throws Exception;

}