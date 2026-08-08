package org.apache.rocketmq.spring.boot.exception;

import org.apache.rocketmq.client.exception.MQClientException;

/**
 * RocketMQ client exception thrown by the starter when consumer/producer
 * initialisation or message processing fails. Extends the native
 * {@link MQClientException}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class RocketMQException extends MQClientException {

    /**
     * @param responseCode  the RocketMQ response code
     * @param errorMessage  the error message
     */
    public RocketMQException(int responseCode, String errorMessage) {
        super(responseCode, errorMessage);
    }

    /**
     * @param e the underlying exception
     */
    public RocketMQException(Exception e) {
        super(e.getMessage(), null);
    }

    /**
     * @param errorMessage the error message
     */
    public RocketMQException(String errorMessage) {
        super(errorMessage, null);
    }

    /**
     * @param errorMessage the error message
     * @param cause        the underlying cause
     */
    public RocketMQException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }


}
