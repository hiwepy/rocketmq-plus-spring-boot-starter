package org.apache.rocketmq.spring.boot.config;

public class DisruptorConfig {


	/**
	 * Enable Disruptor.
	 */
	private boolean enabled = false;
	private int ringBufferSize = 1024;
	private int ringThreadNumbers = 4;
	private boolean multiProducer = false;
	

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isMultiProducer() {
		return multiProducer;
	}

	public void setMultiProducer(boolean multiProducer) {
		this.multiProducer = multiProducer;
	}

	public int getRingBufferSize() {
		return ringBufferSize;
	}

	public void setRingBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}

	public int getRingThreadNumbers() {
		return ringThreadNumbers;
	}

	public void setRingThreadNumbers(int ringThreadNumbers) {
		this.ringThreadNumbers = ringThreadNumbers;
	}

}