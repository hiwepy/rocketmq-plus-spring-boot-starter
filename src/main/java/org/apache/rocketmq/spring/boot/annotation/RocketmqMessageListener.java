package org.apache.rocketmq.spring.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.boot.enums.ConsumeMode;
import org.apache.rocketmq.spring.boot.enums.SelectorType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RocketmqMessageListener {

	String consumerGroup();

	String topic();

	SelectorType selectorType() default SelectorType.TAG;

	String selectorExpress() default "*";

	ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

	MessageModel messageModel() default MessageModel.CLUSTERING;

	int consumeThreadMax() default 64;

}
