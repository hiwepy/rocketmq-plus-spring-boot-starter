package org.apache.rocketmq.spring.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageQueueListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageOrderlyHandler;
import org.junit.jupiter.api.Test;

/**
 * Tests for template classes.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class TemplateTest {

    // ---- RocketmqPullConsumerTemplate ----

    @Test
    void pullTemplate_constructor_setsConsumer() {
        MQPullConsumer consumer = mock(MQPullConsumer.class);
        RocketmqPullConsumerTemplate template = new RocketmqPullConsumerTemplate(consumer);
        assertThat(template.getConsumer()).isEqualTo(consumer);
    }

    @Test
    void pullTemplate_setConsumer() {
        MQPullConsumer consumer = mock(MQPullConsumer.class);
        RocketmqPullConsumerTemplate template = new RocketmqPullConsumerTemplate(consumer);
        MQPullConsumer newConsumer = mock(MQPullConsumer.class);
        template.setConsumer(newConsumer);
        assertThat(template.getConsumer()).isEqualTo(newConsumer);
    }

    @Test
    void pullTemplate_registerMessageListener_delegates() {
        MQPullConsumer consumer = mock(MQPullConsumer.class);
        RocketmqPullConsumerTemplate template = new RocketmqPullConsumerTemplate(consumer);
        MessageQueueListener listener = mock(MessageQueueListener.class);
        template.registerMessageListener("testTopic", listener);
        verify(consumer).registerMessageQueueListener("testTopic", listener);
    }

    // ---- RocketmqPushConsumerTemplate ----

    @Test
    void pushTemplate_constructor_setsConsumer() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        assertThat(template.getConsumer()).isEqualTo(consumer);
    }

    @Test
    void pushTemplate_setConsumer() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        MQPushConsumer newConsumer = mock(MQPushConsumer.class);
        template.setConsumer(newConsumer);
        assertThat(template.getConsumer()).isEqualTo(newConsumer);
    }

    @Test
    void pushTemplate_settersAndGetters() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);

        RocketmqEventMessageOrderlyHandler orderlyHandler = mock(RocketmqEventMessageOrderlyHandler.class);
        template.setMessageOrderlyHandler(orderlyHandler);
        assertThat(template.getMessageOrderlyHandler()).isEqualTo(orderlyHandler);

        RocketmqEventMessageConcurrentlyHandler concurrentlyHandler = mock(RocketmqEventMessageConcurrentlyHandler.class);
        template.setMessageConcurrentlyHandler(concurrentlyHandler);
        assertThat(template.getMessageConcurrentlyHandler()).isEqualTo(concurrentlyHandler);
    }

    @Test
    void pushTemplate_registerMessageListenerConcurrently_delegates() throws Exception {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        MessageListenerConcurrently listener = mock(MessageListenerConcurrently.class);
        template.registerMessageListener(listener);
        verify(consumer).registerMessageListener(listener);
    }

    @Test
    void pushTemplate_registerMessageListenerOrderly_delegates() throws Exception {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        MessageListenerOrderly listener = mock(MessageListenerOrderly.class);
        template.registerMessageListener(listener);
        verify(consumer).registerMessageListener(listener);
    }

    @Test
    void pushTemplate_hashSelector_notNull() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        assertThat(template.HASH_SELECTOR).isNotNull();
    }

    @Test
    void pushTemplate_randomSelector_notNull() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        assertThat(template.RANDOOM_SELECTOR).isNotNull();
    }

    @Test
    void pushTemplate_separator_notNull() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        assertThat(template.SELECTOR_EXPRESSS_EPARATOR).isEqualTo(" || ");
    }

    @Test
    void pushTemplate_getChainResolver_noProperties_returnsNull() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        assertThat(template.getChainResolver()).isNull();
    }

    @Test
    void pushTemplate_unsubscribe_noProperties_returnsEarly() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        RocketmqPushConsumerTemplate template = new RocketmqPushConsumerTemplate(consumer);
        // Should not throw - returns early when chainResolver is null
        template.unsubscribe("topic", "tag", "handler");
    }
}
