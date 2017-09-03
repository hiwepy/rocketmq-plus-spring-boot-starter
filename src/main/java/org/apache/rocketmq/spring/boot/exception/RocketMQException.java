package org.apache.rocketmq.spring.boot.exception;

import org.apache.rocketmq.client.exception.MQClientException;

@SuppressWarnings("serial")
public class RocketMQException extends MQClientException {

    public RocketMQException(int responseCode, String errorMessage) {
        super(responseCode, errorMessage);
    }
    
    public RocketMQException(Exception e) {
        super(e.getMessage(), null);
    }
    
    public RocketMQException(String errorMessage) {
        super(errorMessage, null);
    }
    
    public RocketMQException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
 
    
}
