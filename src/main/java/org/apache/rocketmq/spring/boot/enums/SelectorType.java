package org.apache.rocketmq.spring.boot.enums;


import org.apache.rocketmq.common.filter.ExpressionType;

/**
 * RocketMQ message selector type, controlling how the selector expression is
 * interpreted by the broker.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public enum SelectorType {

    /**
     * Tag-based selector expression.
     * @see ExpressionType#TAG
     */
    TAG,

    /**
     * SQL92-based selector expression.
     * @see ExpressionType#SQL92
     */
    SQL92
}