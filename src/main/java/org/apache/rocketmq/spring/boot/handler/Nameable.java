package org.apache.rocketmq.spring.boot.handler;

/**
 * Contract for components that can be assigned a unique name, used by the
 * handler-chain manager to look up handlers by name.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface Nameable {

	/**
	 * Sets the unique name of this component.
	 *
	 * @param name the unique name
	 */
	void setName(String name);

}
