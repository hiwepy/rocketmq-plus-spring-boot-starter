package org.apache.rocketmq.spring.boot.exception;

/**
 * Runtime exception raised when a RocketMQ {@link org.apache.rocketmq.common.message.Message}
 * cannot be built (e.g. missing topic, tags, keys or body).
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class MessageBuildException extends RuntimeException {

    /**
     * @param e the underlying exception
     */
    public MessageBuildException(Exception e) {
        super(e.getMessage(), null);
    }

    /**
     * @param errorMessage the error message
     */
    public MessageBuildException(String errorMessage) {
        super(errorMessage, null);
    }

    /**
     * @param errorMessage the error message
     * @param cause        the underlying cause
     */
    public MessageBuildException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }


}
