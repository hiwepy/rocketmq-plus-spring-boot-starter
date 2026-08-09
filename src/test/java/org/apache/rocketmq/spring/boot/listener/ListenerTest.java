package org.apache.rocketmq.spring.boot.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.boot.RocketmqPushConsumerProperties;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.MessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.NestedMessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.NestedMessageOrderlyHandler;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

/**
 * Tests for listener classes.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class ListenerTest {

    private MessageExt createMessage() {
        MessageExt msg = new MessageExt();
        msg.setTopic("testTopic");
        msg.setTags("testTag");
        msg.setKeys("testKey");
        msg.setBody(new byte[0]);
        msg.setReconsumeTimes(0);
        return msg;
    }

    // ---- DefaultLocalTransactionExecuter ----

    @Test
    void localTransactionExecuter_firstCall_returnsUnknow() {
        DefaultLocalTransactionExecuter executer = new DefaultLocalTransactionExecuter();
        Message msg = new Message("t", "tag", "key", new byte[0]);
        LocalTransactionState state = executer.executeLocalTransactionBranch(msg, null);
        assertThat(state).isEqualTo(LocalTransactionState.UNKNOW);
    }

    @Test
    void localTransactionExecuter_fifthCall_returnsRollback() {
        DefaultLocalTransactionExecuter executer = new DefaultLocalTransactionExecuter();
        Message msg = new Message("t", "tag", "key", new byte[0]);
        executer.executeLocalTransactionBranch(msg, null); // 1
        executer.executeLocalTransactionBranch(msg, null); // 2
        executer.executeLocalTransactionBranch(msg, null); // 3
        executer.executeLocalTransactionBranch(msg, null); // 4 = COMMIT
        LocalTransactionState state = executer.executeLocalTransactionBranch(msg, null); // 5 = ROLLBACK
        assertThat(state).isEqualTo(LocalTransactionState.ROLLBACK_MESSAGE);
    }

    @Test
    void localTransactionExecuter_fourthCall_returnsCommit() {
        DefaultLocalTransactionExecuter executer = new DefaultLocalTransactionExecuter();
        Message msg = new Message("t", "tag", "key", new byte[0]);
        executer.executeLocalTransactionBranch(msg, null); // 1
        executer.executeLocalTransactionBranch(msg, null); // 2
        executer.executeLocalTransactionBranch(msg, null); // 3
        LocalTransactionState state = executer.executeLocalTransactionBranch(msg, null); // 4 = COMMIT
        assertThat(state).isEqualTo(LocalTransactionState.COMMIT_MESSAGE);
    }

    // ---- DefaultMessageListenerConcurrently ----

    @Test
    void concurrentlyListener_settersAndGetters() {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        listener.setProperties(props);
        assertThat(listener.getProperties()).isEqualTo(props);

        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        listener.setMessageHandler(handler);
        assertThat(listener.getMessageHandler()).isEqualTo(handler);
    }

    @Test
    void concurrentlyListener_setApplicationContext() {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        ApplicationContext ctx = mock(ApplicationContext.class);
        listener.setApplicationContext(ctx);
        assertThat(listener.getApplicationContext()).isEqualTo(ctx);
    }

    @Test
    void concurrentlyListener_consumeMessage_success() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setRetryTimesWhenConsumeFailed(3);
        listener.setProperties(props);

        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        when(handler.preHandle(any(), any())).thenReturn(true);
        listener.setMessageHandler(handler);

        List<MessageExt> msgs = Collections.singletonList(createMessage());
        ConsumeConcurrentlyContext ctx = new ConsumeConcurrentlyContext(new MessageQueue());
        ConsumeConcurrentlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeConcurrentlyStatus.CONSUME_SUCCESS);
        verify(handler).handleMessage(any(), any());
        verify(handler).postHandle(any(), any());
        verify(handler).afterCompletion(any(), any(), any());
    }

    @Test
    void concurrentlyListener_consumeMessage_preHandleFalse_skipsHandle() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        listener.setProperties(props);

        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        when(handler.preHandle(any(), any())).thenReturn(false);
        listener.setMessageHandler(handler);

        List<MessageExt> msgs = Collections.singletonList(createMessage());
        ConsumeConcurrentlyContext ctx = new ConsumeConcurrentlyContext(new MessageQueue());
        ConsumeConcurrentlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeConcurrentlyStatus.CONSUME_SUCCESS);
        verify(handler, never()).handleMessage(any(), any());
    }

    @Test
    void concurrentlyListener_consumeMessage_exception_reconsume() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setRetryTimesWhenConsumeFailed(3);
        listener.setProperties(props);

        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        when(handler.preHandle(any(), any())).thenThrow(new RuntimeException("test error"));
        listener.setMessageHandler(handler);

        MessageExt msg = createMessage();
        msg.setReconsumeTimes(0);
        List<MessageExt> msgs = Collections.singletonList(msg);
        ConsumeConcurrentlyContext ctx = new ConsumeConcurrentlyContext(new MessageQueue());
        ConsumeConcurrentlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeConcurrentlyStatus.RECONSUME_LATER);
    }

    @Test
    void concurrentlyListener_consumeMessage_exception_maxRetry_returnsSuccess() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setRetryTimesWhenConsumeFailed(1);
        listener.setProperties(props);

        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        when(handler.preHandle(any(), any())).thenThrow(new RuntimeException("test error"));
        listener.setMessageHandler(handler);

        MessageExt msg = createMessage();
        msg.setReconsumeTimes(5); // exceeded max retry
        List<MessageExt> msgs = Collections.singletonList(msg);
        ConsumeConcurrentlyContext ctx = new ConsumeConcurrentlyContext(new MessageQueue());
        ConsumeConcurrentlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeConcurrentlyStatus.CONSUME_SUCCESS);
    }

    @Test
    void concurrentlyListener_consumeMessage_exceptionWithCause_reconsume() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setRetryTimesWhenConsumeFailed(3);
        listener.setProperties(props);

        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        when(handler.preHandle(any(), any())).thenThrow(new RuntimeException("wrapper", new Exception("root cause")));
        listener.setMessageHandler(handler);

        MessageExt msg = createMessage();
        msg.setReconsumeTimes(0);
        List<MessageExt> msgs = Collections.singletonList(msg);
        ConsumeConcurrentlyContext ctx = new ConsumeConcurrentlyContext(new MessageQueue());
        ConsumeConcurrentlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeConcurrentlyStatus.RECONSUME_LATER);
    }

    @Test
    void concurrentlyListener_cleanup_afterCompletionThrows() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        doThrow(new RuntimeException("afterCompletion error")).when(handler).afterCompletion(any(), any(), any());
        listener.setMessageHandler(handler);
        listener.cleanup(createMessage(), new ConsumeConcurrentlyContext(new MessageQueue()), null);
    }

    @Test
    void concurrentlyListener_cleanup_afterCompletionThrows_withExistingException() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        MessageConcurrentlyHandler handler = mock(MessageConcurrentlyHandler.class);
        doThrow(new RuntimeException("afterCompletion error")).when(handler).afterCompletion(any(), any(), any());
        listener.setMessageHandler(handler);
        listener.cleanup(createMessage(), new ConsumeConcurrentlyContext(new MessageQueue()), new Exception("existing"));
    }

    // ---- DefaultMessageListenerOrderly ----

    @Test
    void orderlyListener_settersAndGetters() {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        listener.setProperties(props);
        assertThat(listener.getProperties()).isEqualTo(props);

        MessageOrderlyHandler handler = mock(MessageOrderlyHandler.class);
        listener.setMessageHandler(handler);
        assertThat(listener.getMessageHandler()).isEqualTo(handler);
    }

    @Test
    void orderlyListener_setApplicationContext() {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        ApplicationContext ctx = mock(ApplicationContext.class);
        listener.setApplicationContext(ctx);
        assertThat(listener.getApplicationContext()).isEqualTo(ctx);
    }

    @Test
    void orderlyListener_consumeMessage_success() throws Exception {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        listener.setProperties(props);

        MessageOrderlyHandler handler = mock(MessageOrderlyHandler.class);
        when(handler.preHandle(any(), any())).thenReturn(true);
        listener.setMessageHandler(handler);

        List<MessageExt> msgs = Collections.singletonList(createMessage());
        ConsumeOrderlyContext ctx = new ConsumeOrderlyContext(new MessageQueue());
        ConsumeOrderlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeOrderlyStatus.SUCCESS);
        verify(handler).handleMessage(any(), any());
        verify(handler).postHandle(any(), any());
        verify(handler).afterCompletion(any(), any(), any());
    }

    @Test
    void orderlyListener_consumeMessage_preHandleFalse_skipsHandle() throws Exception {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        listener.setProperties(props);

        MessageOrderlyHandler handler = mock(MessageOrderlyHandler.class);
        when(handler.preHandle(any(), any())).thenReturn(false);
        listener.setMessageHandler(handler);

        List<MessageExt> msgs = Collections.singletonList(createMessage());
        ConsumeOrderlyContext ctx = new ConsumeOrderlyContext(new MessageQueue());
        ConsumeOrderlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeOrderlyStatus.SUCCESS);
        verify(handler, never()).handleMessage(any(), any());
    }

    @Test
    void orderlyListener_consumeMessage_exception_suspends() throws Exception {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        listener.setProperties(props);

        MessageOrderlyHandler handler = mock(MessageOrderlyHandler.class);
        when(handler.preHandle(any(), any())).thenThrow(new RuntimeException("test error"));
        listener.setMessageHandler(listener.getMessageHandler());

        // Set up handler after setup
        listener.setMessageHandler(handler);

        List<MessageExt> msgs = Collections.singletonList(createMessage());
        ConsumeOrderlyContext ctx = new ConsumeOrderlyContext(new MessageQueue());
        ConsumeOrderlyStatus status = listener.consumeMessage(msgs, ctx);
        assertThat(status).isEqualTo(ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT);
    }

    @Test
    void orderlyListener_cleanup_afterCompletionThrows() throws Exception {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        MessageOrderlyHandler handler = mock(MessageOrderlyHandler.class);
        doThrow(new RuntimeException("afterCompletion error")).when(handler).afterCompletion(any(), any(), any());
        listener.setMessageHandler(handler);
        listener.cleanup(createMessage(), new ConsumeOrderlyContext(new MessageQueue()), null);
    }

    @Test
    void orderlyListener_cleanup_afterCompletionThrows_withExistingException() throws Exception {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        MessageOrderlyHandler handler = mock(MessageOrderlyHandler.class);
        doThrow(new RuntimeException("afterCompletion error")).when(handler).afterCompletion(any(), any(), any());
        listener.setMessageHandler(handler);
        listener.cleanup(createMessage(), new ConsumeOrderlyContext(new MessageQueue()), new Exception("existing"));
    }

    // ---- DefaultSubscriptionProvider ----

    @Test
    void subscriptionProvider_setApplicationContext() {
        org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider provider =
                new org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider();
        ApplicationContext ctx = mock(ApplicationContext.class);
        provider.setApplicationContext(ctx);
        assertThat(provider.getApplicationContext()).isEqualTo(ctx);
    }

    @Test
    void subscriptionProvider_separator() {
        org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider provider =
                new org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider();
        assertThat(provider.SELECTOR_EXPRESSS_EPARATOR).isEqualTo(" || ");
    }

    @Test
    void subscriptionProvider_subscription_emptyContext() {
        org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider provider =
                new org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider();
        ApplicationContext ctx = mock(ApplicationContext.class);
        when(ctx.getBeansOfType(any(Class.class))).thenReturn(Collections.emptyMap());
        provider.setApplicationContext(ctx);
        java.util.Map<String, String> result = provider.subscription();
        assertThat(result).isEmpty();
    }

    @Test
    void subscriptionProvider_subscription_withConcurrentlyHandler() {
        org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider provider =
                new org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider();
        ApplicationContext ctx = mock(ApplicationContext.class);
        java.util.Map<String, org.apache.rocketmq.spring.boot.handler.EventHandler> handlers = new java.util.HashMap<>();
        handlers.put("handler1", mock(org.apache.rocketmq.spring.boot.handler.EventHandler.class));
        when(ctx.getBeansOfType(org.apache.rocketmq.spring.boot.handler.EventHandler.class)).thenReturn(handlers);
        when(ctx.findAnnotationOnBean(eq("handler1"), eq(org.apache.rocketmq.spring.boot.annotation.RocketmqPushConsumer.class))).thenReturn(null);
        provider.setApplicationContext(ctx);
        java.util.Map<String, String> result = provider.subscription();
        // No annotation on bean, so no subscription
        assertThat(result).isEmpty();
    }

    @Test
    void subscriptionProvider_subscription_withHandler_noAnnotation() {
        org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider provider =
                new org.apache.rocketmq.spring.boot.config.DefaultSubscriptionProvider();
        ApplicationContext ctx = mock(ApplicationContext.class);
        java.util.Map<String, org.apache.rocketmq.spring.boot.handler.EventHandler> handlers = new java.util.HashMap<>();
        handlers.put("handler1", mock(org.apache.rocketmq.spring.boot.handler.EventHandler.class));
        when(ctx.getBeansOfType(org.apache.rocketmq.spring.boot.handler.EventHandler.class)).thenReturn(handlers);
        when(ctx.findAnnotationOnBean(eq("handler1"), eq(org.apache.rocketmq.spring.boot.annotation.RocketmqPushConsumer.class))).thenReturn(null);
        provider.setApplicationContext(ctx);
        java.util.Map<String, String> result = provider.subscription();
        assertThat(result).isEmpty();
    }

    // ---- DefaultMessageListenerConcurrently.afterPropertiesSet ----

    @Test
    void concurrentlyListener_afterPropertiesSet_withBeans() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        ApplicationContext ctx = mock(ApplicationContext.class);
        MessageConcurrentlyHandler regularHandler = mock(MessageConcurrentlyHandler.class);
        NestedMessageConcurrentlyHandler nestedHandler = mock(NestedMessageConcurrentlyHandler.class);
        java.util.Map<String, MessageConcurrentlyHandler> beans = new java.util.LinkedHashMap<>();
        beans.put("regular", regularHandler);
        beans.put("nested", nestedHandler);
        when(ctx.getBeansOfType(MessageConcurrentlyHandler.class)).thenReturn(beans);
        listener.setApplicationContext(ctx);
        listener.afterPropertiesSet();
        assertThat(listener.getMessageHandler()).isNotNull();
    }

    @Test
    void concurrentlyListener_afterPropertiesSet_emptyContext() throws Exception {
        DefaultMessageListenerConcurrently listener = new DefaultMessageListenerConcurrently();
        ApplicationContext ctx = mock(ApplicationContext.class);
        when(ctx.getBeansOfType(MessageConcurrentlyHandler.class)).thenReturn(Collections.emptyMap());
        listener.setApplicationContext(ctx);
        listener.afterPropertiesSet();
        assertThat(listener.getMessageHandler()).isNotNull();
    }

    // ---- DefaultMessageListenerOrderly.afterPropertiesSet ----

    @Test
    void orderlyListener_afterPropertiesSet_withBeans() throws Exception {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        ApplicationContext ctx = mock(ApplicationContext.class);
        MessageOrderlyHandler regularHandler = mock(MessageOrderlyHandler.class);
        NestedMessageOrderlyHandler nestedHandler = mock(NestedMessageOrderlyHandler.class);
        java.util.Map<String, MessageOrderlyHandler> beans = new java.util.LinkedHashMap<>();
        beans.put("regular", regularHandler);
        beans.put("nested", nestedHandler);
        when(ctx.getBeansOfType(MessageOrderlyHandler.class)).thenReturn(beans);
        listener.setApplicationContext(ctx);
        listener.afterPropertiesSet();
        assertThat(listener.getMessageHandler()).isNotNull();
    }

    @Test
    void orderlyListener_afterPropertiesSet_emptyContext() throws Exception {
        DefaultMessageListenerOrderly listener = new DefaultMessageListenerOrderly();
        ApplicationContext ctx = mock(ApplicationContext.class);
        when(ctx.getBeansOfType(MessageOrderlyHandler.class)).thenReturn(Collections.emptyMap());
        listener.setApplicationContext(ctx);
        listener.afterPropertiesSet();
        assertThat(listener.getMessageHandler()).isNotNull();
    }

    // ---- NestedMessageConcurrentlyHandler ----

    @Test
    void nestedConcurrentlyHandler_getHandlers() {
        List<MessageConcurrentlyHandler> handlers = new ArrayList<>();
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(handlers);
        assertThat(handler.getHandlers()).isEqualTo(handlers);
    }

    // ---- NestedMessageOrderlyHandler ----

    @Test
    void nestedOrderlyHandler_getHandlers() {
        List<MessageOrderlyHandler> handlers = new ArrayList<>();
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(handlers);
        assertThat(handler.getHandlers()).isEqualTo(handlers);
    }
}
