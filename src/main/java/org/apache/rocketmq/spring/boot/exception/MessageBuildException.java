package org.apache.rocketmq.spring.boot.exception;

@SuppressWarnings("serial")
public class MessageBuildException extends RuntimeException {

    public MessageBuildException(Exception e) {
        super(e.getMessage(), null);
    }
    
    public MessageBuildException(String errorMessage) {
        super(errorMessage, null);
    }
    
    public MessageBuildException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
 
    
}
