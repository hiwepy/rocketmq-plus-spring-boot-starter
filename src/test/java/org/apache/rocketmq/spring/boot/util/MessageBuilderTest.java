package org.apache.rocketmq.spring.boot.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.boot.exception.MessageBuildException;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MessageBuilder}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class MessageBuilderTest {

    @Test
    void build_validMessage() {
        Message msg = new MessageBuilder()
                .topic("testTopic")
                .tags("testTag")
                .keys("testKey")
                .body("hello")
                .build();

        assertThat(msg.getTopic()).isEqualTo("testTopic");
        assertThat(msg.getTags()).isEqualTo("testTag");
        assertThat(msg.getKeys()).isEqualTo("testKey");
        assertThat(msg.getBody()).isEqualTo("hello".getBytes());
    }

    @Test
    void build_withByteArrayBody() {
        Message msg = new MessageBuilder()
                .topic("t")
                .tags("tag")
                .keys("key")
                .body(new byte[]{1, 2, 3})
                .build();

        assertThat(msg.getBody()).containsExactly(1, 2, 3);
    }

    @Test
    void build_withObjectBody() {
        Message msg = new MessageBuilder()
                .topic("t")
                .tags("tag")
                .keys("key")
                .body(java.util.Collections.singletonMap("k", "v"))
                .build();

        assertThat(msg.getBody()).isNotNull();
    }

    @Test
    void build_missingTopic_throwsException() {
        assertThatThrownBy(() -> new MessageBuilder()
                .tags("tag")
                .keys("key")
                .body("hello")
                .build())
                .isInstanceOf(MessageBuildException.class)
                .hasMessage("topic is empty");
    }

    @Test
    void build_missingTags_throwsException() {
        assertThatThrownBy(() -> new MessageBuilder()
                .topic("t")
                .keys("key")
                .body("hello")
                .build())
                .isInstanceOf(MessageBuildException.class)
                .hasMessage("tags is empty");
    }

    @Test
    void build_missingKeys_throwsException() {
        assertThatThrownBy(() -> new MessageBuilder()
                .topic("t")
                .tags("tag")
                .body("hello")
                .build())
                .isInstanceOf(MessageBuildException.class)
                .hasMessage("keys is empty");
    }

    @Test
    void build_missingBody_throwsException() {
        assertThatThrownBy(() -> new MessageBuilder()
                .topic("t")
                .tags("tag")
                .keys("key")
                .build())
                .isInstanceOf(MessageBuildException.class)
                .hasMessage("body is null");
    }

    @Test
    void builder_chaining() {
        MessageBuilder builder = new MessageBuilder();
        MessageBuilder result = builder.topic("t").tags("tag").keys("key").body("b");
        assertThat(result).isSameAs(builder);
    }
}
