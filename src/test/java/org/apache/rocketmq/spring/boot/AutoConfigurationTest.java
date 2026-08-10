package org.apache.rocketmq.spring.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageOrderlyHandler;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.*;

/**
 * Tests for auto-configuration classes.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class AutoConfigurationTest {

    // ---- RocketmqPushEventHandlerAutoConfiguration ----

    @Test
    void pushEventAutoConfig_setApplicationContext() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        ApplicationContext ctx = mock(ApplicationContext.class);
        config.setApplicationContext(ctx);
        assertThat(config.getApplicationContext()).isEqualTo(ctx);
    }

    @Test
    void pushEventAutoConfig_setHandlerChainDefinitions() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        config.setHandlerChainDefinitions("[urls]\n/test/** = h1\n");
        assertThat(config.getHandlerChainDefinitionMap()).containsKey("/test/**");
    }

    @Test
    void pushEventAutoConfig_setHandlerChainDefinitionMap() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        Map<String, String> map = new HashMap<>();
        map.put("/test/**", "h1");
        config.setHandlerChainDefinitionMap(map);
        assertThat(config.getHandlerChainDefinitionMap()).isEqualTo(map);
    }

    @Test
    void pushEventAutoConfig_getHandlerChainDefinitionMap_defaultEmpty() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        assertThat(config.getHandlerChainDefinitionMap()).isEmpty();
    }

    @Test
    void pushEventAutoConfig_createHandlerChainManager_emptyHandlers() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        HandlerChainManager<RocketmqEvent> manager = config.createHandlerChainManager(new HashMap<>());
        assertThat(manager).isNotNull();
    }

    @Test
    void pushEventAutoConfig_createHandlerChainManager_withHandlers() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
        handlers.put("h1", (event, chain) -> {});
        config.getHandlerChainDefinitionMap().put("/test/**", "h1");
        HandlerChainManager<RocketmqEvent> manager = config.createHandlerChainManager(handlers);
        assertThat(manager).isNotNull();
        assertThat(manager.getHandlers().get("h1")).isNotNull();
    }

    @Test
    void pushEventAutoConfig_rocketmqEventHandlers_emptyContext() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        ApplicationContext ctx = mock(ApplicationContext.class);
        when(ctx.getBeansOfType(EventHandler.class)).thenReturn(new HashMap<>());
        config.setApplicationContext(ctx);
        Map<String, EventHandler<RocketmqEvent>> handlers = config.rocketmqEventHandlers();
        assertThat(handlers).isEmpty();
    }

    @Test
    void pushEventAutoConfig_messageConcurrentlyHandler_withDefinitions() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        RocketmqPushEventHandlerDefinitionProperties props = new RocketmqPushEventHandlerDefinitionProperties();
        props.setDefinitions("[urls]\n/test/** = h1\n");
        Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
        handlers.put("h1", (event, chain) -> {});
        RocketmqEventMessageConcurrentlyHandler handler = config.messageConcurrentlyHandler(props, handlers);
        assertThat(handler).isNotNull();
    }

    @Test
    void pushEventAutoConfig_messageConcurrentlyHandler_withDefinitionMap() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        RocketmqPushEventHandlerDefinitionProperties props = new RocketmqPushEventHandlerDefinitionProperties();
        Map<String, String> defMap = new HashMap<>();
        defMap.put("/test/**", "h1");
        props.setDefinitionMap(defMap);
        Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
        handlers.put("h1", (event, chain) -> {});
        RocketmqEventMessageConcurrentlyHandler handler = config.messageConcurrentlyHandler(props, handlers);
        assertThat(handler).isNotNull();
    }

    @Test
    void pushEventAutoConfig_messageOrderlyHandler_withDefinitions() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        RocketmqPushEventHandlerDefinitionProperties props = new RocketmqPushEventHandlerDefinitionProperties();
        props.setDefinitions("[urls]\n/test/** = h1\n");
        Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
        handlers.put("h1", (event, chain) -> {});
        RocketmqEventMessageOrderlyHandler handler = config.messageOrderlyHandler(props, handlers);
        assertThat(handler).isNotNull();
    }

    @Test
    void pushEventAutoConfig_messageOrderlyHandler_withDefinitionMap() {
        RocketmqPushEventHandlerAutoConfiguration config = new RocketmqPushEventHandlerAutoConfiguration();
        RocketmqPushEventHandlerDefinitionProperties props = new RocketmqPushEventHandlerDefinitionProperties();
        Map<String, String> defMap = new HashMap<>();
        defMap.put("/test/**", "h1");
        props.setDefinitionMap(defMap);
        Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
        handlers.put("h1", (event, chain) -> {});
        RocketmqEventMessageOrderlyHandler handler = config.messageOrderlyHandler(props, handlers);
        assertThat(handler).isNotNull();
    }

    // ---- RocketmqPullConsumerAutoConfiguration ----

    @Test
    void pullAutoConfig_setApplicationContext() {
        RocketmqPullConsumerAutoConfiguration config = new RocketmqPullConsumerAutoConfiguration();
        ApplicationContext ctx = mock(ApplicationContext.class);
        config.setApplicationContext(ctx);
        assertThat(config.getApplicationContext()).isEqualTo(ctx);
    }

    @Test
    void pullAutoConfig_getApplicationContext() {
        RocketmqPullConsumerAutoConfiguration config = new RocketmqPullConsumerAutoConfiguration();
        assertThat(config.getApplicationContext()).isNull();
    }

    @Test
    void pullAutoConfig_allocateMessageQueueStrategy() {
        RocketmqPullConsumerAutoConfiguration config = new RocketmqPullConsumerAutoConfiguration();
        AllocateMessageQueueStrategy strategy = config.allocateMessageQueueStrategy();
        assertThat(strategy).isNotNull();
    }

    @Test
    void pullAutoConfig_configure_setsProperties() throws Exception {
        RocketmqPullConsumerAutoConfiguration config = new RocketmqPullConsumerAutoConfiguration();
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("testGroup");
        RocketmqPullConsumerProperties props = new RocketmqPullConsumerProperties();
        props.setConsumerGroup("testGroup");
        props.setNamesrvAddr("localhost:9876");
        props.setBrokerSuspendMaxTimeMillis(15000);
        props.setConsumerPullTimeoutMillis(5000);
        props.setConsumerTimeoutMillisWhenSuspend(20000);
        props.setMaxReconsumeTimes(3);
        props.setRegisterTopics(new java.util.HashSet<>(java.util.Arrays.asList("topic1")));
        config.configure(consumer, props);
        assertThat(consumer.getConsumerGroup()).isEqualTo("testGroup");
    }

    @Test
    void pullAutoConfig_rocketmqConsumerTemplate() throws Exception {
        RocketmqPullConsumerAutoConfiguration config = new RocketmqPullConsumerAutoConfiguration();
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("testGroup");
        RocketmqPullConsumerTemplate template = config.rocketmqConsumerTemplate(consumer);
        assertThat(template).isNotNull();
        assertThat(template.getConsumer()).isEqualTo(consumer);
    }

    // ---- RocketmqPushConsumerAutoConfiguration ----

    @Test
    void pushAutoConfig_hasDefaultMethods() {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        assertThat(config).isNotNull();
    }

    @Test
    void pushAutoConfig_allocateMessageQueueStrategy() {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        AllocateMessageQueueStrategy strategy = config.allocateMessageQueueStrategy();
        assertThat(strategy).isNotNull();
    }

    @Test
    void pushAutoConfig_defaultSubProvider() {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        org.apache.rocketmq.spring.boot.config.SubscriptionProvider provider = config.defaultSubProvider();
        assertThat(provider).isNotNull();
    }

    @Test
    void pushAutoConfig_messageListenerConcurrently() {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently listener = config.messageListenerConcurrently();
        assertThat(listener).isNotNull();
    }

    @Test
    void pushAutoConfig_messageListenerOrderly() {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly listener = config.messageListenerOrderly();
        assertThat(listener).isNotNull();
    }

    @Test
    void pushAutoConfig_configure_setsProperties() throws Exception {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("testGroup");
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setConsumerGroup("testGroup");
        props.setNamesrvAddr("localhost:9876");
        props.setConsumeThreadMin(10);
        props.setConsumeThreadMax(20);
        props.setConsumeTimeout(10);
        props.setMaxReconsumeTimes(3);
        config.configure(consumer, props);
        assertThat(consumer.getConsumerGroup()).isEqualTo("testGroup");
    }

    @Test
    void pushAutoConfig_rocketmqConsumerTemplate() throws Exception {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("testGroup");
        RocketmqPushConsumerTemplate template = config.rocketmqConsumerTemplate(consumer);
        assertThat(template).isNotNull();
        assertThat(template.getConsumer()).isEqualTo(consumer);
    }

    @Test
    void pushAutoConfig_configure_setsAllProperties() throws Exception {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("testGroup");
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setConsumerGroup("testGroup");
        props.setNamesrvAddr("localhost:9876");
        props.setConsumeThreadMin(5);
        props.setConsumeThreadMax(15);
        props.setConsumeTimeout(5);
        props.setMaxReconsumeTimes(5);
        props.setClientIP("127.0.0.1");
        props.setInstanceName("testInstance");
        props.setHeartbeatBrokerInterval(30000);
        props.setConsumeConcurrentlyMaxSpan(2000);
        props.setConsumeMessageBatchMaxSize(32);
        props.setPullBatchSize(32);
        props.setPullInterval(1000);
        props.setPullThresholdForQueue(1000);
        props.setSuspendCurrentQueueTimeMillis(1000);
        props.setUnitMode(true);
        props.setUnitName("testUnit");
        props.setVipChannelEnabled(false);
        props.setPostSubscriptionWhenPull(true);
        props.setPersistConsumerOffsetInterval(5000);
        props.setPollNameServerInterval(30000);
        props.setAdjustThreadPoolNumsThreshold(100);
        props.setClientCallbackExecutorThreads(8);
        config.configure(consumer, props);
        assertThat(consumer.getConsumerGroup()).isEqualTo("testGroup");
        assertThat(consumer.getNamesrvAddr()).isEqualTo("localhost:9876");
    }

    @Test
    void pullAutoConfig_configure_setsAllProperties() throws Exception {
        RocketmqPullConsumerAutoConfiguration config = new RocketmqPullConsumerAutoConfiguration();
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("testGroup");
        RocketmqPullConsumerProperties props = new RocketmqPullConsumerProperties();
        props.setConsumerGroup("testGroup");
        props.setNamesrvAddr("localhost:9876");
        props.setBrokerSuspendMaxTimeMillis(20000);
        props.setConsumerPullTimeoutMillis(10000);
        props.setConsumerTimeoutMillisWhenSuspend(30000);
        props.setMaxReconsumeTimes(5);
        props.setClientIP("127.0.0.1");
        props.setInstanceName("testInstance");
        props.setHeartbeatBrokerInterval(30000);
        props.setClientCallbackExecutorThreads(8);
        props.setPersistConsumerOffsetInterval(5000);
        props.setPollNameServerInterval(30000);
        props.setUnitMode(true);
        props.setUnitName("testUnit");
        props.setVipChannelEnabled(false);
        props.setRegisterTopics(new java.util.HashSet<>(java.util.Arrays.asList("topic1", "topic2")));
        config.configure(consumer, props);
        assertThat(consumer.getConsumerGroup()).isEqualTo("testGroup");
        assertThat(consumer.getNamesrvAddr()).isEqualTo("localhost:9876");
    }

    @Test
    void pullAutoConfig_configure_withInvalidMessageModel() throws Exception {
        RocketmqPullConsumerAutoConfiguration config = new RocketmqPullConsumerAutoConfiguration();
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("testGroup");
        RocketmqPullConsumerProperties props = new RocketmqPullConsumerProperties();
        props.setConsumerGroup("testGroup");
        props.setNamesrvAddr("localhost:9876");
        props.setMessageModel("INVALID_MODEL");
        config.configure(consumer, props);
        // Should fall back to CLUSTERING
        assertThat(consumer.getMessageModel()).isEqualTo(org.apache.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING);
    }

    @Test
    void pushAutoConfig_configure_withInvalidConsumeFromWhere() throws Exception {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("testGroup");
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setConsumerGroup("testGroup");
        props.setNamesrvAddr("localhost:9876");
        props.setConsumeFromWhere("INVALID_WHERE");
        config.configure(consumer, props);
        // Should fall back to CONSUME_FROM_FIRST_OFFSET
        assertThat(consumer.getConsumeFromWhere()).isEqualTo(org.apache.rocketmq.common.consumer.ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
    }

    @Test
    void pushAutoConfig_configure_withInvalidMessageModel() throws Exception {
        RocketmqPushConsumerAutoConfiguration config = new RocketmqPushConsumerAutoConfiguration();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("testGroup");
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        props.setConsumerGroup("testGroup");
        props.setNamesrvAddr("localhost:9876");
        props.setMessageModel("INVALID_MODEL");
        config.configure(consumer, props);
        // Should fall back to CLUSTERING
        assertThat(consumer.getMessageModel()).isEqualTo(org.apache.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING);
    }
}
