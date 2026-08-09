package org.apache.rocketmq.spring.boot.disruptor;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;
import org.junit.jupiter.api.Test;

/**
 * Tests for disruptor-related classes.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class DisruptorTest {

    @Test
    void eventFactory_newInstance_createsEvent() {
        RocketmqDataEventFactory factory = new RocketmqDataEventFactory();
        RocketmqDisruptorEvent event = factory.newInstance();
        assertThat(event).isNotNull();
        assertThat(event.getSource()).isEqualTo(factory);
    }

    @Test
    void concurrentlyEventTranslator_translateTo_setsFields() throws Exception {
        ConsumeConcurrentlyContext ctx = new ConsumeConcurrentlyContext(new MessageQueue());
        RocketmqDataConcurrentlyEventTranslator translator = new RocketmqDataConcurrentlyEventTranslator(ctx);
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        MessageExt msg = new MessageExt();
        msg.setTopic("testTopic");
        msg.setTags("testTag");
        msg.setKeys("testKey");
        msg.setBody(new byte[]{1, 2, 3});

        translator.translateTo(event, 0, msg);

        assertThat(event.getMessageExt()).isEqualTo(msg);
        assertThat(event.getTopic()).isEqualTo("testTopic");
        assertThat(event.getTag()).isEqualTo("testTag");
        assertThat(event.getBody()).isEqualTo(new byte[]{1, 2, 3});
        assertThat(translator.getContext()).isEqualTo(ctx);
    }

    @Test
    void concurrentlyEventTranslator_setContext() throws Exception {
        RocketmqDataConcurrentlyEventTranslator translator = new RocketmqDataConcurrentlyEventTranslator(null);
        ConsumeConcurrentlyContext ctx = new ConsumeConcurrentlyContext(new MessageQueue());
        translator.setContext(ctx);
        assertThat(translator.getContext()).isEqualTo(ctx);
    }

    @Test
    void orderlyEventTranslator_translateTo_setsFields() throws Exception {
        ConsumeOrderlyContext ctx = new ConsumeOrderlyContext(new MessageQueue());
        RocketmqDataOrderlyEventTranslator translator = new RocketmqDataOrderlyEventTranslator(ctx);
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        MessageExt msg = new MessageExt();
        msg.setTopic("testTopic");
        msg.setTags("testTag");
        msg.setKeys("testKey");
        msg.setBody(new byte[]{4, 5, 6});

        translator.translateTo(event, 0, msg);

        assertThat(event.getMessageExt()).isEqualTo(msg);
        assertThat(event.getTopic()).isEqualTo("testTopic");
        assertThat(event.getTag()).isEqualTo("testTag");
        assertThat(event.getBody()).isEqualTo(new byte[]{4, 5, 6});
        assertThat(translator.getContext()).isEqualTo(ctx);
    }

    @Test
    void orderlyEventTranslator_setContext() throws Exception {
        RocketmqDataOrderlyEventTranslator translator = new RocketmqDataOrderlyEventTranslator(null);
        ConsumeOrderlyContext ctx = new ConsumeOrderlyContext(new MessageQueue());
        translator.setContext(ctx);
        assertThat(translator.getContext()).isEqualTo(ctx);
    }
}
