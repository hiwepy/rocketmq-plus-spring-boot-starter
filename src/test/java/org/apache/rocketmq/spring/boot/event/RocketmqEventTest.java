package org.apache.rocketmq.spring.boot.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RocketmqEvent}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class RocketmqEventTest {

    private MessageExt createMessageExt(String topic, String tags, String keys, byte[] body) {
        MessageExt msg = new MessageExt();
        msg.setTopic(topic);
        msg.setTags(tags);
        msg.setKeys(keys);
        msg.setBody(body);
        return msg;
    }

    @Test
    void constructor_setsFields() throws Exception {
        MessageExt msg = createMessageExt("testTopic", "testTag", "testKey", "hello".getBytes());
        MessageQueue mq = new MessageQueue("testTopic", "broker-a", 0);

        RocketmqEvent event = new RocketmqEvent(msg, mq);

        assertThat(event.getMessageExt()).isEqualTo(msg);
        assertThat(event.getMessageQueue()).isEqualTo(mq);
        assertThat(event.getTopic()).isEqualTo("testTopic");
        assertThat(event.getTag()).isEqualTo("testTag");
        assertThat(event.getBody()).isEqualTo("hello".getBytes());
        assertThat(event.getRouteExpression()).isEqualTo("/testTopic/testTag/testKey");
    }

    @Test
    void getMsgBody_utf8_returnsDecoded() throws Exception {
        MessageExt msg = createMessageExt("t", "tag", "key", "hello".getBytes("UTF-8"));
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());

        assertThat(event.getMsgBody()).isEqualTo("hello");
    }

    @Test
    void getMsgBody_withCharset_returnsDecoded() throws Exception {
        MessageExt msg = createMessageExt("t", "tag", "key", "hello".getBytes("UTF-8"));
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());

        assertThat(event.getMsgBody("UTF-8")).isEqualTo("hello");
    }

    @Test
    void setters_updateFields() throws Exception {
        MessageExt msg = createMessageExt("t", "tag", "key", new byte[0]);
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());

        event.setTopic("newTopic");
        event.setTag("newTag");
        event.setBody(new byte[]{1, 2, 3});
        event.setRouteExpression("/custom/path");

        assertThat(event.getTopic()).isEqualTo("newTopic");
        assertThat(event.getTag()).isEqualTo("newTag");
        assertThat(event.getBody()).hasSize(3);
        assertThat(event.getRouteExpression()).isEqualTo("/custom/path");
    }

    @Test
    void getRouteExpression_autoBuiltFromMessage() throws Exception {
        MessageExt msg = createMessageExt("orders", "created", "order-123", new byte[0]);
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());

        assertThat(event.getRouteExpression()).isEqualTo("/orders/created/order-123");
    }

    @Test
    void testEventCreation() throws Exception {
        MessageExt msg = createMessageExt("topic", "tag", "key", new byte[0]);
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());
        assertThat(event).isNotNull();
    }

    @Test
    void constructor_withNullTagAndKey() throws Exception {
        MessageExt msg = new MessageExt();
        msg.setTopic("topic");
        msg.setTags(null);
        msg.setKeys((String) null);
        msg.setBody(new byte[0]);
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());
        assertThat(event.getRouteExpression()).isEqualTo("/topic/null/null");
    }
}
