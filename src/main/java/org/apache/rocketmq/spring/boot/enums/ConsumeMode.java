package org.apache.rocketmq.spring.boot.enums;

public enum ConsumeMode {
    /**
     * receive asynchronously delivered messages concurrently
     */
    CONCURRENTLY,

    /**
     * receive asynchronously delivered messages orderly. one queue, one thread
     */
    ORDERLY
}