package org.apache.rocketmq.spring.boot;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.rocketmq.spring.boot.enums.ConsumeMode;
import org.apache.rocketmq.spring.boot.enums.SelectorType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tests for {@link RocketmqPushConsumerProperties}, {@link RocketmqPullConsumerProperties},
 * {@link RocketmqPushEventHandlerDefinitionProperties},
 * {@link ConsumeMode}, and {@link SelectorType}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class PropertiesTest {

    // ---- RocketmqPushConsumerProperties ----

    @Test
    void pushProperties_defaults() {
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();
        assertThat(props.isEnabled()).isFalse();
        assertThat(props.getConsumerGroup()).isNull();
        assertThat(props.getMessageModel()).isEqualTo("CLUSTERING");
        assertThat(props.getConsumeFromWhere()).isEqualTo("CONSUME_FROM_LAST_OFFSET");
        assertThat(props.getConsumeMode()).isEqualTo(ConsumeMode.CONCURRENTLY);
        assertThat(props.getSelectorType()).isEqualTo(SelectorType.TAG);
        assertThat(props.getSubscription()).isEmpty();
        assertThat(props.getConsumeThreadMin()).isEqualTo(20);
        assertThat(props.getConsumeThreadMax()).isEqualTo(64);
        assertThat(props.getAdjustThreadPoolNumsThreshold()).isEqualTo(100000L);
        assertThat(props.getConsumeConcurrentlyMaxSpan()).isEqualTo(2000);
        assertThat(props.getPullThresholdForQueue()).isEqualTo(1000);
        assertThat(props.getPullInterval()).isZero();
        assertThat(props.getConsumeMessageBatchMaxSize()).isEqualTo(1);
        assertThat(props.getPullBatchSize()).isEqualTo(32);
        assertThat(props.isPostSubscriptionWhenPull()).isFalse();
        assertThat(props.getMaxReconsumeTimes()).isEqualTo(-1);
        assertThat(props.getSuspendCurrentQueueTimeMillis()).isEqualTo(1000L);
        assertThat(props.getConsumeTimeout()).isEqualTo(15L);
        assertThat(props.getRetryTimesWhenConsumeFailed()).isEqualTo(3);
        assertThat(props.getDelayLevelWhenNextConsume()).isZero();
        assertThat(props.getDelayStartSeconds()).isEqualTo(10);
        assertThat(props.getConsumeTimestamp()).isNotNull();
    }

    @Test
    void pushProperties_setters() {
        RocketmqPushConsumerProperties props = new RocketmqPushConsumerProperties();

        props.setEnabled(true);
        assertThat(props.isEnabled()).isTrue();

        props.setConsumerGroup("myGroup");
        assertThat(props.getConsumerGroup()).isEqualTo("myGroup");

        props.setMessageModel("BROADCASTING");
        assertThat(props.getMessageModel()).isEqualTo("BROADCASTING");

        props.setConsumeFromWhere("CONSUME_FROM_FIRST_OFFSET");
        assertThat(props.getConsumeFromWhere()).isEqualTo("CONSUME_FROM_FIRST_OFFSET");

        props.setConsumeTimestamp("20230101120000");
        assertThat(props.getConsumeTimestamp()).isEqualTo("20230101120000");

        props.setConsumeMode(ConsumeMode.ORDERLY);
        assertThat(props.getConsumeMode()).isEqualTo(ConsumeMode.ORDERLY);

        props.setSelectorType(SelectorType.SQL92);
        assertThat(props.getSelectorType()).isEqualTo(SelectorType.SQL92);

        Map<String, String> sub = new HashMap<>();
        sub.put("topic1", "*");
        props.setSubscription(sub);
        assertThat(props.getSubscription()).hasSize(1);

        props.setConsumeThreadMin(5);
        assertThat(props.getConsumeThreadMin()).isEqualTo(5);

        props.setConsumeThreadMax(10);
        assertThat(props.getConsumeThreadMax()).isEqualTo(10);

        props.setAdjustThreadPoolNumsThreshold(50000L);
        assertThat(props.getAdjustThreadPoolNumsThreshold()).isEqualTo(50000L);

        props.setConsumeConcurrentlyMaxSpan(1000);
        assertThat(props.getConsumeConcurrentlyMaxSpan()).isEqualTo(1000);

        props.setPullThresholdForQueue(500);
        assertThat(props.getPullThresholdForQueue()).isEqualTo(500);

        props.setPullInterval(100L);
        assertThat(props.getPullInterval()).isEqualTo(100L);

        props.setConsumeMessageBatchMaxSize(10);
        assertThat(props.getConsumeMessageBatchMaxSize()).isEqualTo(10);

        props.setPullBatchSize(16);
        assertThat(props.getPullBatchSize()).isEqualTo(16);

        props.setPostSubscriptionWhenPull(true);
        assertThat(props.isPostSubscriptionWhenPull()).isTrue();

        props.setMaxReconsumeTimes(5);
        assertThat(props.getMaxReconsumeTimes()).isEqualTo(5);

        props.setSuspendCurrentQueueTimeMillis(2000L);
        assertThat(props.getSuspendCurrentQueueTimeMillis()).isEqualTo(2000L);

        props.setConsumeTimeout(30L);
        assertThat(props.getConsumeTimeout()).isEqualTo(30L);

        props.setRetryTimesWhenConsumeFailed(5);
        assertThat(props.getRetryTimesWhenConsumeFailed()).isEqualTo(5);

        props.setDelayLevelWhenNextConsume(3);
        assertThat(props.getDelayLevelWhenNextConsume()).isEqualTo(3);

        props.setDelayStartSeconds(20);
        assertThat(props.getDelayStartSeconds()).isEqualTo(20);
    }

    @Test
    void pushProperties_prefix() {
        assertThat(RocketmqPushConsumerProperties.PREFIX).isEqualTo("rocketmq.consume-passively");
    }

    // ---- RocketmqPullConsumerProperties ----

    @Test
    void pullProperties_defaults() {
        RocketmqPullConsumerProperties props = new RocketmqPullConsumerProperties();
        assertThat(props.isEnabled()).isFalse();
        assertThat(props.isSchedulable()).isFalse();
        assertThat(props.getConsumerGroup()).isNull();
        assertThat(props.getMessageModel()).isEqualTo("CLUSTERING");
        assertThat(props.getRegisterTopics()).isEmpty();
        assertThat(props.getBrokerSuspendMaxTimeMillis()).isEqualTo(20000L);
        assertThat(props.getConsumerTimeoutMillisWhenSuspend()).isEqualTo(30000L);
        assertThat(props.getConsumerPullTimeoutMillis()).isEqualTo(10000L);
        assertThat(props.getPullThreadNums()).isEqualTo(20);
        assertThat(props.getPullNextDelayTimeMillis()).isEqualTo(200);
        assertThat(props.getMaxReconsumeTimes()).isEqualTo(-1);
        assertThat(props.getDelayStartSeconds()).isEqualTo(10);
    }

    @Test
    void pullProperties_setters() {
        RocketmqPullConsumerProperties props = new RocketmqPullConsumerProperties();

        props.setEnabled(true);
        assertThat(props.isEnabled()).isTrue();

        props.setSchedulable(true);
        assertThat(props.isSchedulable()).isTrue();

        props.setConsumerGroup("myGroup");
        assertThat(props.getConsumerGroup()).isEqualTo("myGroup");

        props.setMessageModel("BROADCASTING");
        assertThat(props.getMessageModel()).isEqualTo("BROADCASTING");

        Set<String> topics = new HashSet<>();
        topics.add("topic1");
        props.setRegisterTopics(topics);
        assertThat(props.getRegisterTopics()).hasSize(1);

        props.setBrokerSuspendMaxTimeMillis(10000L);
        assertThat(props.getBrokerSuspendMaxTimeMillis()).isEqualTo(10000L);

        props.setConsumerTimeoutMillisWhenSuspend(15000L);
        assertThat(props.getConsumerTimeoutMillisWhenSuspend()).isEqualTo(15000L);

        props.setConsumerPullTimeoutMillis(5000L);
        assertThat(props.getConsumerPullTimeoutMillis()).isEqualTo(5000L);

        props.setPullThreadNums(10);
        assertThat(props.getPullThreadNums()).isEqualTo(10);

        props.setPullNextDelayTimeMillis(100);
        assertThat(props.getPullNextDelayTimeMillis()).isEqualTo(100);

        props.setMaxReconsumeTimes(3);
        assertThat(props.getMaxReconsumeTimes()).isEqualTo(3);

        props.setDelayStartSeconds(5);
        assertThat(props.getDelayStartSeconds()).isEqualTo(5);
    }

    @Test
    void pullProperties_prefix() {
        assertThat(RocketmqPullConsumerProperties.PREFIX).isEqualTo("rocketmq.consume-actively");
    }

    // ---- RocketmqPushEventHandlerDefinitionProperties ----

    @Test
    void pushEventHandlerProperties_defaults() {
        RocketmqPushEventHandlerDefinitionProperties props = new RocketmqPushEventHandlerDefinitionProperties();
        assertThat(props.getDefinitions()).isNull();
        assertThat(props.getDefinitionMap()).isEmpty();
    }

    @Test
    void pushEventHandlerProperties_setters() {
        RocketmqPushEventHandlerDefinitionProperties props = new RocketmqPushEventHandlerDefinitionProperties();

        props.setDefinitions("[main]\nh1,h2");
        assertThat(props.getDefinitions()).isEqualTo("[main]\nh1,h2");

        Map<String, String> map = new HashMap<>();
        map.put("/test/**", "h1,h2");
        props.setDefinitionMap(map);
        assertThat(props.getDefinitionMap()).hasSize(1);
    }

    @Test
    void pushEventHandlerProperties_prefix() {
        assertThat(RocketmqPushEventHandlerDefinitionProperties.PREFIX)
                .isEqualTo("rocketmq.consume-passively.event");
    }

    // ---- ConsumeMode enum ----

    @Test
    void consumeMode_values() {
        assertThat(ConsumeMode.values()).hasSize(2);
        assertThat(ConsumeMode.valueOf("CONCURRENTLY")).isEqualTo(ConsumeMode.CONCURRENTLY);
        assertThat(ConsumeMode.valueOf("ORDERLY")).isEqualTo(ConsumeMode.ORDERLY);
    }

    // ---- SelectorType enum ----

    @Test
    void selectorType_values() {
        assertThat(SelectorType.values()).hasSize(2);
        assertThat(SelectorType.valueOf("TAG")).isEqualTo(SelectorType.TAG);
        assertThat(SelectorType.valueOf("SQL92")).isEqualTo(SelectorType.SQL92);
    }
}
