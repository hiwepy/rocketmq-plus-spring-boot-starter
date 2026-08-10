package org.apache.rocketmq.spring.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds an {@link org.apache.rocketmq.spring.boot.handler.EventHandler} to an
 * Ant-style event dispatch rule of the form {@code topic/tags/keys}, e.g.
 * {@code topic-a/tag-a/*}. The rule is used by the handler-chain resolver to
 * route incoming messages.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RocketmqPushConsumer {

	/**
	 * @return the topic segment of the dispatch rule
	 */
	String topic();

	/**
	 * @return the tags segment of the dispatch rule
	 */
	String tags();

	/**
	 * @return the keys segment of the dispatch rule (defaults to wildcard {@code "*"})
	 */
	String keys() default "*";

}
