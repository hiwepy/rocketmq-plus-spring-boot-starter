package org.apache.rocketmq.spring.boot.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EventHandleException}, {@link MessageBuildException}, and {@link RocketMQException}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class ExceptionTest {

    // ---- EventHandleException ----

    @Test
    void eventHandleException_withMessage() {
        EventHandleException ex = new EventHandleException("test error");
        assertThat(ex.getMessage()).isEqualTo("test error");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void eventHandleException_withCause() {
        Exception cause = new Exception("root cause");
        EventHandleException ex = new EventHandleException(cause);
        assertThat(ex.getMessage()).isEqualTo("root cause");
    }

    @Test
    void eventHandleException_withMessageAndCause() {
        Exception cause = new Exception("root");
        EventHandleException ex = new EventHandleException("test error", cause);
        assertThat(ex.getMessage()).isEqualTo("test error");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    // ---- MessageBuildException ----

    @Test
    void messageBuildException_withMessage() {
        MessageBuildException ex = new MessageBuildException("build error");
        assertThat(ex.getMessage()).isEqualTo("build error");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void messageBuildException_withCause() {
        Exception cause = new Exception("root cause");
        MessageBuildException ex = new MessageBuildException(cause);
        assertThat(ex.getMessage()).isEqualTo("root cause");
    }

    @Test
    void messageBuildException_withMessageAndCause() {
        Exception cause = new Exception("root");
        MessageBuildException ex = new MessageBuildException("build error", cause);
        assertThat(ex.getMessage()).isEqualTo("build error");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    // ---- RocketMQException ----

    @Test
    void rocketMQException_withCodeAndMessage() {
        RocketMQException ex = new RocketMQException(100, "error");
        assertThat(ex.getResponseCode()).isEqualTo(100);
        assertThat(ex.getErrorMessage()).isEqualTo("error");
    }

    @Test
    void rocketMQException_withMessage() {
        RocketMQException ex = new RocketMQException("test error");
        assertThat(ex.getErrorMessage()).isEqualTo("test error");
    }

    @Test
    void rocketMQException_withCause() {
        Exception cause = new Exception("root cause");
        RocketMQException ex = new RocketMQException(cause);
        assertThat(ex.getErrorMessage()).isEqualTo("root cause");
    }

    @Test
    void rocketMQException_withMessageAndCause() {
        Exception cause = new Exception("root");
        RocketMQException ex = new RocketMQException("test error", cause);
        assertThat(ex.getErrorMessage()).isEqualTo("test error");
        assertThat(ex.getCause()).isEqualTo(cause);
    }
}
