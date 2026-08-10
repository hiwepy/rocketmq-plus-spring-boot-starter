package org.apache.rocketmq.spring.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link org.apache.rocketmq.client.consumer.PullTaskCallback} bean as
 * a pull-task callback bound to the given topic. The scheduled pull consumer
 * auto-configuration registers the callback against the topic it declares.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RocketmqPullCallback {

	/**
	 * @return the topic this pull-task callback subscribes to
	 */
	String topic();

}