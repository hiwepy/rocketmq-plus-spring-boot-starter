package org.apache.rocketmq.spring.boot.enums;


import org.apache.rocketmq.common.filter.ExpressionType;

/**
 * Selector Type
 */
public enum SelectorType {

    /**
     * @see ExpressionType#TAG
     */
    TAG,

    /**
     * @see ExpressionType#SQL92
     */
    SQL92
}