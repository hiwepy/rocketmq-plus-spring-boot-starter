package org.apache.rocketmq.spring.boot.enums;

/**
 * RocketMQ message consumption mode, determining how the broker delivers
 * messages to the consumer.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public enum ConsumeMode {
    /**
     * Receive asynchronously delivered messages concurrently using a thread pool.
     */
    CONCURRENTLY,

    /**
     * Receive asynchronously delivered messages orderly: one queue, one thread.
     */
    ORDERLY
}