package org.apache.rocketmq.spring.boot.handler;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;

/**
 * 给Handler设置路径
 */
public interface PathProcessor<T extends RocketmqEvent> {
	
	EventHandler<T> processPath(String path);

}
