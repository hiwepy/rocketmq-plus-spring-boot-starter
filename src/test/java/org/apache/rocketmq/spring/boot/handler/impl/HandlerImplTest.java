package org.apache.rocketmq.spring.boot.handler.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.MessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.MessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.chain.def.PathMatchingHandlerChainResolver;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Tests for handler implementation classes.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class HandlerImplTest {

    private MessageExt createMessage() {
        MessageExt msg = new MessageExt();
        msg.setTopic("testTopic");
        msg.setTags("testTag");
        msg.setKeys("testKey");
        msg.setBody(new byte[0]);
        return msg;
    }

    private ConsumeConcurrentlyContext createConcurrentlyContext() {
        return new ConsumeConcurrentlyContext(new MessageQueue("testTopic", "broker-a", 0));
    }

    private ConsumeOrderlyContext createOrderlyContext() {
        return new ConsumeOrderlyContext(new MessageQueue("testTopic", "broker-a", 0));
    }

    // ---- ApplicationEventMessageConcurrentlyHandler ----

    @Test
    void appEventConcurrentlyHandler_preHandle_returnsTrue() throws Exception {
        ApplicationEventMessageConcurrentlyHandler handler = new ApplicationEventMessageConcurrentlyHandler();
        assertThat(handler.preHandle(createMessage(), createConcurrentlyContext())).isTrue();
    }

    @Test
    void appEventConcurrentlyHandler_setApplicationEventPublisher() {
        ApplicationEventMessageConcurrentlyHandler handler = new ApplicationEventMessageConcurrentlyHandler();
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        handler.setApplicationEventPublisher(publisher);
        assertThat(handler.getEventPublisher()).isEqualTo(publisher);
    }

    @Test
    void appEventConcurrentlyHandler_handleMessage_publishesEvent() throws Exception {
        ApplicationEventMessageConcurrentlyHandler handler = new ApplicationEventMessageConcurrentlyHandler();
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        handler.setApplicationEventPublisher(publisher);
        handler.handleMessage(createMessage(), createConcurrentlyContext());
        verify(publisher).publishEvent(any(RocketmqEvent.class));
    }

    @Test
    void appEventConcurrentlyHandler_afterCompletion_noException() throws Exception {
        ApplicationEventMessageConcurrentlyHandler handler = new ApplicationEventMessageConcurrentlyHandler();
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), null);
    }

    @Test
    void appEventConcurrentlyHandler_afterCompletion_withException() throws Exception {
        ApplicationEventMessageConcurrentlyHandler handler = new ApplicationEventMessageConcurrentlyHandler();
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), new Exception("test"));
    }

    @Test
    void appEventConcurrentlyHandler_postHandle_noOp() throws Exception {
        ApplicationEventMessageConcurrentlyHandler handler = new ApplicationEventMessageConcurrentlyHandler();
        handler.postHandle(createMessage(), createConcurrentlyContext());
    }

    // ---- ApplicationEventMessageOrderlyHandler ----

    @Test
    void appEventOrderlyHandler_preHandle_returnsTrue() throws Exception {
        ApplicationEventMessageOrderlyHandler handler = new ApplicationEventMessageOrderlyHandler();
        assertThat(handler.preHandle(createMessage(), createOrderlyContext())).isTrue();
    }

    @Test
    void appEventOrderlyHandler_setApplicationEventPublisher() {
        ApplicationEventMessageOrderlyHandler handler = new ApplicationEventMessageOrderlyHandler();
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        handler.setApplicationEventPublisher(publisher);
        assertThat(handler.getEventPublisher()).isEqualTo(publisher);
    }

    @Test
    void appEventOrderlyHandler_handleMessage_publishesEvent() throws Exception {
        ApplicationEventMessageOrderlyHandler handler = new ApplicationEventMessageOrderlyHandler();
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        handler.setApplicationEventPublisher(publisher);
        handler.handleMessage(createMessage(), createOrderlyContext());
        verify(publisher).publishEvent(any(RocketmqEvent.class));
    }

    @Test
    void appEventOrderlyHandler_afterCompletion_noException() throws Exception {
        ApplicationEventMessageOrderlyHandler handler = new ApplicationEventMessageOrderlyHandler();
        handler.afterCompletion(createMessage(), createOrderlyContext(), null);
    }

    @Test
    void appEventOrderlyHandler_afterCompletion_withException() throws Exception {
        ApplicationEventMessageOrderlyHandler handler = new ApplicationEventMessageOrderlyHandler();
        handler.afterCompletion(createMessage(), createOrderlyContext(), new Exception("test"));
    }

    @Test
    void appEventOrderlyHandler_postHandle_noOp() throws Exception {
        ApplicationEventMessageOrderlyHandler handler = new ApplicationEventMessageOrderlyHandler();
        handler.postHandle(createMessage(), createOrderlyContext());
    }

    // ---- RocketmqEventMessageConcurrentlyHandler ----

    @Test
    void rocketmqEventConcurrentlyHandler_preHandle_returnsTrue() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageConcurrentlyHandler handler = new RocketmqEventMessageConcurrentlyHandler(resolver);
        assertThat(handler.preHandle(createMessage(), createConcurrentlyContext())).isTrue();
    }

    @Test
    void rocketmqEventConcurrentlyHandler_handleMessage_noException() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageConcurrentlyHandler handler = new RocketmqEventMessageConcurrentlyHandler(resolver);
        handler.handleMessage(createMessage(), createConcurrentlyContext());
    }

    @Test
    void rocketmqEventConcurrentlyHandler_afterCompletion_noException() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageConcurrentlyHandler handler = new RocketmqEventMessageConcurrentlyHandler(resolver);
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), null);
    }

    @Test
    void rocketmqEventConcurrentlyHandler_afterCompletion_withException() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageConcurrentlyHandler handler = new RocketmqEventMessageConcurrentlyHandler(resolver);
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), new Exception("test"));
    }

    @Test
    void rocketmqEventConcurrentlyHandler_postHandle_noOp() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageConcurrentlyHandler handler = new RocketmqEventMessageConcurrentlyHandler(resolver);
        handler.postHandle(createMessage(), createConcurrentlyContext());
    }

    // ---- RocketmqEventMessageOrderlyHandler ----

    @Test
    void rocketmqEventOrderlyHandler_preHandle_returnsTrue() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageOrderlyHandler handler = new RocketmqEventMessageOrderlyHandler(resolver);
        assertThat(handler.preHandle(createMessage(), createOrderlyContext())).isTrue();
    }

    @Test
    void rocketmqEventOrderlyHandler_handleMessage_noException() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageOrderlyHandler handler = new RocketmqEventMessageOrderlyHandler(resolver);
        handler.handleMessage(createMessage(), createOrderlyContext());
    }

    @Test
    void rocketmqEventOrderlyHandler_afterCompletion_noException() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageOrderlyHandler handler = new RocketmqEventMessageOrderlyHandler(resolver);
        handler.afterCompletion(createMessage(), createOrderlyContext(), null);
    }

    @Test
    void rocketmqEventOrderlyHandler_afterCompletion_withException() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageOrderlyHandler handler = new RocketmqEventMessageOrderlyHandler(resolver);
        handler.afterCompletion(createMessage(), createOrderlyContext(), new Exception("test"));
    }

    @Test
    void rocketmqEventOrderlyHandler_postHandle_noOp() throws Exception {
        HandlerChainResolver<RocketmqEvent> resolver = new PathMatchingHandlerChainResolver();
        RocketmqEventMessageOrderlyHandler handler = new RocketmqEventMessageOrderlyHandler(resolver);
        handler.postHandle(createMessage(), createOrderlyContext());
    }

    // ---- NestedMessageConcurrentlyHandler ----

    @Test
    void nestedConcurrentlyHandler_preHandle_returnsTrue() throws Exception {
        List<MessageConcurrentlyHandler> handlers = new ArrayList<>();
        handlers.add(new ApplicationEventMessageConcurrentlyHandler());
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(handlers);
        assertThat(handler.preHandle(createMessage(), createConcurrentlyContext())).isTrue();
    }

    @Test
    void nestedConcurrentlyHandler_emptyHandlers_throwsOnHandle() {
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(new ArrayList<>());
        assertThatThrownBy(() -> handler.handleMessage(createMessage(), createConcurrentlyContext()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nestedConcurrentlyHandler_withHandlers_delegates() throws Exception {
        MessageConcurrentlyHandler inner = mock(MessageConcurrentlyHandler.class);
        List<MessageConcurrentlyHandler> handlers = Collections.singletonList(inner);
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(handlers);
        handler.handleMessage(createMessage(), createConcurrentlyContext());
        verify(inner).handleMessage(any(MessageExt.class), any(ConsumeConcurrentlyContext.class));
    }

    @Test
    void nestedConcurrentlyHandler_afterCompletion_noException() throws Exception {
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(new ArrayList<>());
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), null);
    }

    @Test
    void nestedConcurrentlyHandler_afterCompletion_withException() throws Exception {
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(new ArrayList<>());
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), new Exception("test"));
    }

    @Test
    void nestedConcurrentlyHandler_postHandle_noOp() throws Exception {
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(new ArrayList<>());
        handler.postHandle(createMessage(), createConcurrentlyContext());
    }

    @Test
    void nestedConcurrentlyHandler_getHandlers() {
        List<MessageConcurrentlyHandler> handlers = new ArrayList<>();
        NestedMessageConcurrentlyHandler handler = new NestedMessageConcurrentlyHandler(handlers);
        assertThat(handler.getHandlers()).isEqualTo(handlers);
    }

    // ---- NestedMessageOrderlyHandler ----

    @Test
    void nestedOrderlyHandler_preHandle_returnsTrue() throws Exception {
        List<MessageOrderlyHandler> handlers = new ArrayList<>();
        handlers.add(new ApplicationEventMessageOrderlyHandler());
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(handlers);
        assertThat(handler.preHandle(createMessage(), createOrderlyContext())).isTrue();
    }

    @Test
    void nestedOrderlyHandler_emptyHandlers_throwsOnHandle() {
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(new ArrayList<>());
        assertThatThrownBy(() -> handler.handleMessage(createMessage(), createOrderlyContext()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nestedOrderlyHandler_withHandlers_delegates() throws Exception {
        MessageOrderlyHandler inner = mock(MessageOrderlyHandler.class);
        List<MessageOrderlyHandler> handlers = Collections.singletonList(inner);
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(handlers);
        handler.handleMessage(createMessage(), createOrderlyContext());
        verify(inner).handleMessage(any(MessageExt.class), any(ConsumeOrderlyContext.class));
    }

    @Test
    void nestedOrderlyHandler_afterCompletion_noException() throws Exception {
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(new ArrayList<>());
        handler.afterCompletion(createMessage(), createOrderlyContext(), null);
    }

    @Test
    void nestedOrderlyHandler_afterCompletion_withException() throws Exception {
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(new ArrayList<>());
        handler.afterCompletion(createMessage(), createOrderlyContext(), new Exception("test"));
    }

    @Test
    void nestedOrderlyHandler_postHandle_noOp() throws Exception {
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(new ArrayList<>());
        handler.postHandle(createMessage(), createOrderlyContext());
    }

    @Test
    void nestedOrderlyHandler_getHandlers() {
        List<MessageOrderlyHandler> handlers = new ArrayList<>();
        NestedMessageOrderlyHandler handler = new NestedMessageOrderlyHandler(handlers);
        assertThat(handler.getHandlers()).isEqualTo(handlers);
    }

    // ---- DisruptorEventMessageConcurrentlyHandler ----

    @Test
    void disruptorConcurrentlyHandler_preHandle_returnsTrue() throws Exception {
        DisruptorEventMessageConcurrentlyHandler handler = new DisruptorEventMessageConcurrentlyHandler();
        assertThat(handler.preHandle(createMessage(), createConcurrentlyContext())).isTrue();
    }

    @Test
    void disruptorConcurrentlyHandler_afterCompletion_noException() throws Exception {
        DisruptorEventMessageConcurrentlyHandler handler = new DisruptorEventMessageConcurrentlyHandler();
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), null);
    }

    @Test
    void disruptorConcurrentlyHandler_afterCompletion_withException() throws Exception {
        DisruptorEventMessageConcurrentlyHandler handler = new DisruptorEventMessageConcurrentlyHandler();
        handler.afterCompletion(createMessage(), createConcurrentlyContext(), new Exception("test"));
    }

    @Test
    void disruptorConcurrentlyHandler_postHandle_noOp() throws Exception {
        DisruptorEventMessageConcurrentlyHandler handler = new DisruptorEventMessageConcurrentlyHandler();
        handler.postHandle(createMessage(), createConcurrentlyContext());
    }

    // ---- DisruptorEventMessageOrderlyHandler ----

    @Test
    void disruptorOrderlyHandler_preHandle_returnsTrue() throws Exception {
        DisruptorEventMessageOrderlyHandler handler = new DisruptorEventMessageOrderlyHandler();
        assertThat(handler.preHandle(createMessage(), createOrderlyContext())).isTrue();
    }

    @Test
    void disruptorOrderlyHandler_afterCompletion_noException() throws Exception {
        DisruptorEventMessageOrderlyHandler handler = new DisruptorEventMessageOrderlyHandler();
        handler.afterCompletion(createMessage(), createOrderlyContext(), null);
    }

    @Test
    void disruptorOrderlyHandler_afterCompletion_withException() throws Exception {
        DisruptorEventMessageOrderlyHandler handler = new DisruptorEventMessageOrderlyHandler();
        handler.afterCompletion(createMessage(), createOrderlyContext(), new Exception("test"));
    }

    @Test
    void disruptorOrderlyHandler_postHandle_noOp() throws Exception {
        DisruptorEventMessageOrderlyHandler handler = new DisruptorEventMessageOrderlyHandler();
        handler.postHandle(createMessage(), createOrderlyContext());
    }
}
