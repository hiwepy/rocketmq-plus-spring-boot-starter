package org.apache.rocketmq.spring.boot.hooks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.junit.jupiter.api.Test;

/**
 * Tests for shutdown hooks.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class ShutdownHookTest {

    @Test
    void producerShutdownHook_run_callsShutdown() {
        MQProducer producer = mock(MQProducer.class);
        MQProducerShutdownHook hook = new MQProducerShutdownHook(producer);
        hook.run();
        verify(producer).shutdown();
    }

    @Test
    void producerShutdownHook_isThread() {
        MQProducer producer = mock(MQProducer.class);
        MQProducerShutdownHook hook = new MQProducerShutdownHook(producer);
        assertThat(hook).isInstanceOf(Thread.class);
    }

    @Test
    void pushConsumerShutdownHook_run_callsShutdown() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        MQPushConsumerShutdownHook hook = new MQPushConsumerShutdownHook(consumer);
        hook.run();
        verify(consumer).shutdown();
    }

    @Test
    void pushConsumerShutdownHook_isThread() {
        MQPushConsumer consumer = mock(MQPushConsumer.class);
        MQPushConsumerShutdownHook hook = new MQPushConsumerShutdownHook(consumer);
        assertThat(hook).isInstanceOf(Thread.class);
    }

    @Test
    void pullConsumerShutdownHook_run_callsShutdown() {
        MQPullConsumer consumer = mock(MQPullConsumer.class);
        MQPullConsumerShutdownHook hook = new MQPullConsumerShutdownHook(consumer);
        hook.run();
        verify(consumer).shutdown();
    }

    @Test
    void pullConsumerShutdownHook_isThread() {
        MQPullConsumer consumer = mock(MQPullConsumer.class);
        MQPullConsumerShutdownHook hook = new MQPullConsumerShutdownHook(consumer);
        assertThat(hook).isInstanceOf(Thread.class);
    }

    @Test
    void pullConsumerScheduleShutdownHook_run_callsShutdown() {
        MQPullConsumerScheduleService scheduleService = mock(MQPullConsumerScheduleService.class);
        MQPullConsumerScheduleShutdownHook hook = new MQPullConsumerScheduleShutdownHook(scheduleService);
        hook.run();
        verify(scheduleService).shutdown();
    }

    @Test
    void pullConsumerScheduleShutdownHook_isThread() {
        MQPullConsumerScheduleService scheduleService = mock(MQPullConsumerScheduleService.class);
        MQPullConsumerScheduleShutdownHook hook = new MQPullConsumerScheduleShutdownHook(scheduleService);
        assertThat(hook).isInstanceOf(Thread.class);
    }
}
