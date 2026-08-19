package org.apache.rocketmq.spring.boot.disruptor;

import org.apache.rocketmq.spring.boot.event.RocketmqDisruptorEvent;

import com.lmax.disruptor.EventFactory;

/**
 * Disruptor {@link EventFactory} that produces fresh
 * {@link RocketmqDisruptorEvent} instances for the ring buffer.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class RocketmqDataEventFactory implements EventFactory<RocketmqDisruptorEvent> {

	@Override
    /**
     * <p>New instance.</p>
     * @return the new instance
     */
	public RocketmqDisruptorEvent newInstance() {
		return new RocketmqDisruptorEvent(this);
	}

}
