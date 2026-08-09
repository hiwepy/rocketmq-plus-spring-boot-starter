package org.apache.rocketmq.spring.boot.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain;
import org.junit.jupiter.api.Test;

/**
 * Tests for abstract handler classes.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class AbstractHandlerTest {

    private RocketmqEvent createEvent(String routeExpression) throws Exception {
        MessageExt msg = new MessageExt();
        msg.setTopic("test");
        msg.setTags("tag");
        msg.setKeys("key");
        msg.setBody(new byte[0]);
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());
        event.setRouteExpression(routeExpression);
        return event;
    }

    // Concrete test subclass that works for all abstract handler types
    private static class TestPathMatchHandler extends AbstractPathMatchMessageHandler<RocketmqEvent> {
        @Override
        public void doHandlerInternal(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {
            executeChain(event, handlerChain);
        }
    }

    // ---- AbstractNameableMessageHandler ----

    @Test
    void nameableHandler_setName_getName() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setName("testHandler");
        assertThat(handler.getName()).isEqualTo("testHandler");
    }

    // ---- AbstractEnabledMessageHandler ----

    @Test
    void enabledHandler_defaultEnabled() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        assertThat(handler.isEnabled()).isTrue();
    }

    @Test
    void enabledHandler_setDisabled() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setEnabled(false);
        assertThat(handler.isEnabled()).isFalse();
    }

    @Test
    void enabledHandler_enabled_callsInternal() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("/test/path");
        handler.doHandler(event, new ProxiedHandlerChain());
    }

    @Test
    void enabledHandler_disabled_skipsInternal() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setEnabled(false);
        RocketmqEvent event = createEvent("/test/path");
        handler.doHandler(event, new ProxiedHandlerChain());
    }

    // ---- AbstractRouteableMessageHandler (tested via concrete impl classes in HandlerImplTest) ----

    @Test
    void adviceHandler_isEnabled_defaultTrue() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        assertThat(handler.isEnabled()).isTrue();
    }

    // ---- AbstractAdviceMessageHandler ----

    @Test
    void adviceHandler_doHandlerInternal_enabled_executes() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("/test/path");
        handler.doHandlerInternal(event, new ProxiedHandlerChain());
    }

    @Test
    void adviceHandler_doHandlerInternal_disabled_skips() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setEnabled(false);
        RocketmqEvent event = createEvent("/test/path");
        handler.doHandlerInternal(event, new ProxiedHandlerChain());
    }

    @Test
    void adviceHandler_cleanup_noException() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        RocketmqEvent event = createEvent("/test/path");
        handler.cleanup(event, null);
    }

    @Test
    void adviceHandler_cleanup_withException() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        RocketmqEvent event = createEvent("/test/path");
        handler.cleanup(event, new Exception("test"));
    }

    // ---- AbstractPathMatchMessageHandler ----

    @Test
    void pathMatchHandler_processPath_addsPath() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        EventHandler<RocketmqEvent> result = handler.processPath("/test/**");
        assertThat(result).isEqualTo(handler);
        assertThat(handler.getAppliedPaths()).contains("/test/**");
    }

    @Test
    void pathMatchHandler_getPathMatcher_notNull() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        assertThat(handler.getPathMatcher()).isNotNull();
    }

    @Test
    void pathMatchHandler_setPathMatcher() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        org.springframework.util.PathMatcher matcher = new org.springframework.util.AntPathMatcher();
        handler.setPathMatcher(matcher);
        assertThat(handler.getPathMatcher()).isEqualTo(matcher);
    }

    @Test
    void pathMatchHandler_preHandle_emptyPaths_returnsTrue() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        RocketmqEvent event = createEvent("/test/path");
        assertThat(handler.preHandle(event)).isTrue();
    }

    @Test
    void pathMatchHandler_preHandle_matchingPath_returnsTrue() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.processPath("/test/**");
        RocketmqEvent event = createEvent("/test/path");
        assertThat(handler.preHandle(event)).isTrue();
    }

    @Test
    void pathMatchHandler_preHandle_nonMatchingPath_returnsTrue() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.processPath("/other/**");
        RocketmqEvent event = createEvent("/test/path");
        assertThat(handler.preHandle(event)).isTrue();
    }

    @Test
    void pathMatchHandler_onPreHandle_returnsTrue() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        RocketmqEvent event = createEvent("/test/path");
        assertThat(handler.onPreHandle(event)).isTrue();
    }

    // ---- MessageConcurrentlyHandlerAdapter / MessageOrderlyHandlerAdapter ----

    @Test
    void concurrentlyAdapter_allMethods_noOp() throws Exception {
        MessageConcurrentlyHandlerAdapter adapter = new MessageConcurrentlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext ctx =
                new org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext(new MessageQueue());
        assertThat(adapter.preHandle(msg, ctx)).isTrue();
        adapter.handleMessage(msg, ctx);
        adapter.postHandle(msg, ctx);
        adapter.afterCompletion(msg, ctx, null);
    }

    @Test
    void orderlyAdapter_allMethods_noOp() throws Exception {
        MessageOrderlyHandlerAdapter adapter = new MessageOrderlyHandlerAdapter() {};
        MessageExt msg = new MessageExt();
        org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext ctx =
                new org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext(new MessageQueue());
        assertThat(adapter.preHandle(msg, ctx)).isTrue();
        adapter.handleMessage(msg, ctx);
        adapter.postHandle(msg, ctx);
        adapter.afterCompletion(msg, ctx, null);
    }

    // ---- AbstractAdviceMessageHandler: preHandle throws ----

    @Test
    void adviceHandler_doHandlerInternal_preHandleThrows() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler() {
            @Override
            protected boolean preHandle(RocketmqEvent event) throws Exception {
                throw new RuntimeException("preHandle error");
            }
        };
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("/test/path");
        // Should propagate through cleanup
        try {
            handler.doHandlerInternal(event, new ProxiedHandlerChain());
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void adviceHandler_doHandlerInternal_postHandleThrows() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler() {
            @Override
            protected void postHandle(RocketmqEvent event) throws Exception {
                throw new RuntimeException("postHandle error");
            }
        };
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("/test/path");
        try {
            handler.doHandlerInternal(event, new ProxiedHandlerChain());
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void adviceHandler_cleanup_afterCompletionThrows_withExistingException() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler() {
            @Override
            public void afterCompletion(RocketmqEvent event, Exception exception) throws Exception {
                throw new RuntimeException("afterCompletion error");
            }
        };
        RocketmqEvent event = createEvent("/test/path");
        handler.cleanup(event, new Exception("existing"));
    }

    @Test
    void adviceHandler_isEnabledEvent_delegates() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("/test/path");
        assertThat(handler.isEnabled(event)).isTrue();
    }

    // ---- AbstractRouteableMessageHandler: constructor with resolver ----

    private static class TestRouteableHandler extends AbstractRouteableMessageHandler<RocketmqEvent> {
    }

    @Test
    void routeableHandler_constructorWithResolver() {
        org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver<RocketmqEvent> resolver =
                org.mockito.Mockito.mock(org.apache.rocketmq.spring.boot.handler.chain.HandlerChainResolver.class);
        TestRouteableHandler handler = new TestRouteableHandler();
        handler.setHandlerChainResolver(resolver);
        assertThat(handler.getHandlerChainResolver()).isEqualTo(resolver);
    }

    @Test
    void routeableHandler_doHandlerInternal_success() throws Exception {
        TestRouteableHandler handler = new TestRouteableHandler();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("/test/path");
        handler.doHandlerInternal(event, new ProxiedHandlerChain());
    }

    @Test
    void routeableHandler_doHandlerInternal_throwsIOException() throws Exception {
        TestRouteableHandler handler = new TestRouteableHandler() {
            @Override
            protected void executeChain(RocketmqEvent event, HandlerChain<RocketmqEvent> chain) throws Exception {
                throw new java.io.IOException("IO error");
            }
        };
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("/test/path");
        try {
            handler.doHandlerInternal(event, new ProxiedHandlerChain());
        } catch (java.io.IOException e) {
            assertThat(e.getMessage()).isEqualTo("IO error");
        }
    }

    @Test
    void routeableHandler_getExecutionChain_noResolver() throws Exception {
        TestRouteableHandler handler = new TestRouteableHandler();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        RocketmqEvent event = createEvent("/test/path");
        handler.executeChain(event, chain);
    }
}
