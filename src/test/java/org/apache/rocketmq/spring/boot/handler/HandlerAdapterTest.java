package org.apache.rocketmq.spring.boot.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MessageConcurrentlyHandlerAdapter}, {@link MessageOrderlyHandlerAdapter},
 * {@link AbstractNameableMessageHandler}, and {@link AbstractEnabledMessageHandler}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class HandlerAdapterTest {

    // ---- MessageConcurrentlyHandlerAdapter ----

    @Test
    void concurrentlyAdapter_preHandle_returnsTrue() throws Exception {
        MessageConcurrentlyHandlerAdapter adapter = new MessageConcurrentlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(new MessageQueue());
        assertThat(adapter.preHandle(msg, context)).isTrue();
    }

    @Test
    void concurrentlyAdapter_handleMessage_noOp() throws Exception {
        MessageConcurrentlyHandlerAdapter adapter = new MessageConcurrentlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(new MessageQueue());
        // Should not throw
        adapter.handleMessage(msg, context);
    }

    @Test
    void concurrentlyAdapter_postHandle_noOp() throws Exception {
        MessageConcurrentlyHandlerAdapter adapter = new MessageConcurrentlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(new MessageQueue());
        adapter.postHandle(msg, context);
    }

    @Test
    void concurrentlyAdapter_afterCompletion_noOp() throws Exception {
        MessageConcurrentlyHandlerAdapter adapter = new MessageConcurrentlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(new MessageQueue());
        adapter.afterCompletion(msg, context, null);
    }

    // ---- MessageOrderlyHandlerAdapter ----

    @Test
    void orderlyAdapter_preHandle_returnsTrue() throws Exception {
        MessageOrderlyHandlerAdapter adapter = new MessageOrderlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeOrderlyContext context = new ConsumeOrderlyContext(new MessageQueue());
        assertThat(adapter.preHandle(msg, context)).isTrue();
    }

    @Test
    void orderlyAdapter_handleMessage_noOp() throws Exception {
        MessageOrderlyHandlerAdapter adapter = new MessageOrderlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeOrderlyContext context = new ConsumeOrderlyContext(new MessageQueue());
        adapter.handleMessage(msg, context);
    }

    @Test
    void orderlyAdapter_postHandle_noOp() throws Exception {
        MessageOrderlyHandlerAdapter adapter = new MessageOrderlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeOrderlyContext context = new ConsumeOrderlyContext(new MessageQueue());
        adapter.postHandle(msg, context);
    }

    @Test
    void orderlyAdapter_afterCompletion_noOp() throws Exception {
        MessageOrderlyHandlerAdapter adapter = new MessageOrderlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        ConsumeOrderlyContext context = new ConsumeOrderlyContext(new MessageQueue());
        adapter.afterCompletion(msg, context, null);
    }

    // ---- AbstractNameableMessageHandler ----

    @Test
    void nameableHandler_setName_getName() {
        AbstractNameableMessageHandler<RocketmqEvent> handler = new AbstractNameableMessageHandler<RocketmqEvent>() {
            @Override
            public void doHandler(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {}
        };
        handler.setName("testHandler");
        assertThat(handler.getName()).isEqualTo("testHandler");
    }

    // ---- AbstractEnabledMessageHandler ----

    @Test
    void enabledHandler_defaultEnabled() {
        AbstractEnabledMessageHandler<RocketmqEvent> handler = new AbstractEnabledMessageHandler<RocketmqEvent>() {
            @Override
            protected void doHandlerInternal(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {}
        };
        assertThat(handler.isEnabled()).isTrue();
    }

    @Test
    void enabledHandler_setDisabled() {
        AbstractEnabledMessageHandler<RocketmqEvent> handler = new AbstractEnabledMessageHandler<RocketmqEvent>() {
            @Override
            protected void doHandlerInternal(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {}
        };
        handler.setEnabled(false);
        assertThat(handler.isEnabled()).isFalse();
    }

    @Test
    void enabledHandler_enabled_callsInternal() throws Exception {
        boolean[] called = {false};
        AbstractEnabledMessageHandler<RocketmqEvent> handler = new AbstractEnabledMessageHandler<RocketmqEvent>() {
            @Override
            protected void doHandlerInternal(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {
                called[0] = true;
            }
        };
        handler.setEnabled(true);
        MessageExt msg = new MessageExt();
        msg.setTopic("t");
        msg.setTags("tag");
        msg.setKeys("key");
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());
        handler.doHandler(event, new org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain());
        assertThat(called[0]).isTrue();
    }

    @Test
    void enabledHandler_disabled_skipsInternal() throws Exception {
        boolean[] called = {false};
        AbstractEnabledMessageHandler<RocketmqEvent> handler = new AbstractEnabledMessageHandler<RocketmqEvent>() {
            @Override
            protected void doHandlerInternal(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {
                called[0] = true;
            }
        };
        handler.setEnabled(false);
        MessageExt msg = new MessageExt();
        msg.setTopic("t");
        msg.setTags("tag");
        msg.setKeys("key");
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());
        handler.doHandler(event, new org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain());
        assertThat(called[0]).isFalse();
    }
}
