package org.apache.rocketmq.spring.boot.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Concurrent message handler that publishes a {@link RocketmqEvent} through the
 * Spring {@link ApplicationEventPublisher}, allowing tag-specific listeners to
 * receive it.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class ApplicationEventMessageConcurrentlyHandler implements MessageConcurrentlyHandler, ApplicationEventPublisherAware {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationEventMessageConcurrentlyHandler.class);
	private ApplicationEventPublisher eventPublisher;

	@Override
    /**
     * <p>Pre handle.</p>
     * @param msgExt
     * @param context
     * @return the pre handle
     */
	public boolean preHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		return true;
	}

	@Override
    /**
     * <p>Handle message.</p>
     * @param msgExt
     * @param context
     */
	public void handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		// Publish a message-arrived event so tag-specific listeners can handle it.
		getEventPublisher().publishEvent(new RocketmqEvent(msgExt, context.getMessageQueue()));
	}
	
	@Override
    /**
     * <p>Post handle.</p>
     * @param msgExt
     * @param context
     */
	public void postHandle(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		
	}

	@Override
    /**
     * <p>After completion.</p>
     * @param msgExt
     * @param context
     * @param ex
     */
	public void afterCompletion(MessageExt msgExt, ConsumeConcurrentlyContext context, Exception ex) throws Exception {
		if(ex != null) {
			LOG.warn("Consume message failed. messageExt:{}", msgExt, ex);
		}
	}

	@Override
    /**
     * <p>Sets the application event publisher.</p>
     * @param applicationEventPublisher
     */
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

    /**
     * <p>Returns the event publisher.</p>
     * @return the get event publisher
     */
	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}

}