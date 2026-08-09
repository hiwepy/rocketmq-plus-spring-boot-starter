package org.apache.rocketmq.spring.boot.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RocketmqDisruptorEvent}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class RocketmqDisruptorEventTest {

    private MessageExt createMessageExt(String topic, String tags, String keys, byte[] body) {
        MessageExt msg = new MessageExt();
        msg.setTopic(topic);
        msg.setTags(tags);
        msg.setKeys(keys);
        msg.setBody(body);
        return msg;
    }

    @Test
    void constructor_setsSource() {
        Object source = new Object();
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(source);
        assertThat(event.getSource()).isEqualTo(source);
    }

    @Test
    void getRouteExpression_noMessage_returnsNull() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        assertThat(event.getRouteExpression()).isNull();
    }

    @Test
    void getRouteExpression_withMessage_autoBuilds() {
        MessageExt msg = createMessageExt("orders", "created", "order-123", new byte[0]);
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        event.setMessageExt(msg);

        assertThat(event.getRouteExpression()).isEqualTo("/orders/created/order-123");
    }

    @Test
    void getRouteExpression_explicitlySet_returnsSet() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        event.setRouteExpression("/custom/path");

        assertThat(event.getRouteExpression()).isEqualTo("/custom/path");
    }

    @Test
    void settersAndGetters() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);

        MessageExt msg = createMessageExt("t", "tag", "key", new byte[]{1, 2});
        event.setMessageExt(msg);
        event.setTopic("myTopic");
        event.setTag("myTag");
        event.setBody(new byte[]{3, 4, 5});

        assertThat(event.getMessageExt()).isEqualTo(msg);
        assertThat(event.getTopic()).isEqualTo("myTopic");
        assertThat(event.getTag()).isEqualTo("myTag");
        assertThat(event.getBody()).containsExactly(3, 4, 5);
    }

    @Test
    void getMsgBody_utf8_returnsDecoded() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        event.setBody("hello".getBytes());

        assertThat(event.getMsgBody()).isEqualTo("hello");
    }

    @Test
    void getMsgBody_withCharset_returnsDecoded() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        event.setBody("hello".getBytes());

        assertThat(event.getMsgBody("UTF-8")).isEqualTo("hello");
    }

    @Test
    void getRouteExpression_cachedAfterFirstCall() {
        MessageExt msg = createMessageExt("orders", "created", "key1", new byte[0]);
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(this);
        event.setMessageExt(msg);

        String first = event.getRouteExpression();
        String second = event.getRouteExpression();

        assertThat(first).isEqualTo(second);
    }
}
