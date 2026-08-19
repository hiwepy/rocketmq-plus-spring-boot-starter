package org.apache.rocketmq.spring.boot;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Handler-chain definition properties for the RocketMQ push consumer.
 * <p>
 * Bound to the {@code rocketmq.consume-passively.event.*} namespace. Allows
 * handler chains to be declared either as a single INI-style string
 * ({@link #definitions}) or as a {@code rule -> handler names} map
 * ({@link #definitionMap}).
 * </p>
 *
 * <h3>Configuration keys</h3>
 * <ul>
 *   <li>{@code rocketmq.consume-passively.event.definitions} — INI-style chain definitions</li>
 *   <li>{@code rocketmq.consume-passively.event.definition-map} — rule-to-handler-names map</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@ConfigurationProperties(RocketmqPushEventHandlerDefinitionProperties.PREFIX)
public class RocketmqPushEventHandlerDefinitionProperties {
	
	/** Configuration prefix, nested under the push consumer namespace. */
	public static final String PREFIX = RocketmqPushConsumerProperties.PREFIX + ".event";

	/** INI-style handler-chain definitions. */
	private String definitions = null;

    /** Rule-to-handler-names mapping used to build handler chains. */
    private Map<String /* rule */, String /* handler names */> definitionMap = new LinkedHashMap<String, String>();

    /**
     * <p>Returns the definitions.</p>
     * @return the get definitions
     */
	public String getDefinitions() {
		return definitions;
	}

    /**
     * <p>Sets the definitions.</p>
     * @param definitions
     */
	public void setDefinitions(String definitions) {
		this.definitions = definitions;
	}

    /**
     * <p>Returns the definition map.</p>
     * @return the get definition map
     */
	public Map<String, String> getDefinitionMap() {
		return definitionMap;
	}

    /**
     * <p>Sets the definition map.</p>
     * @param definitionMap
     */
	public void setDefinitionMap(Map<String, String> definitionMap) {
		this.definitionMap = definitionMap;
	}

	
    
    
}
