package org.apache.rocketmq.spring.boot.exception;

/**
 * Runtime exception raised when an {@link org.apache.rocketmq.spring.boot.handler.EventHandler}
 * fails to process a message.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class EventHandleException extends RuntimeException {

    /**
     * @param e the underlying exception
     */
    public EventHandleException(Exception e) {
        super(e.getMessage(), null);
    }

    /**
     * @param errorMessage the error message
     */
    public EventHandleException(String errorMessage) {
        super(errorMessage, null);
    }

    /**
     * @param errorMessage the error message
     * @param cause        the underlying cause
     */
    public EventHandleException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }


}
