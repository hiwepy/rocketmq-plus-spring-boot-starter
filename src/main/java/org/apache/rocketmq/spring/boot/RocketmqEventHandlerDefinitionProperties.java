package org.apache.rocketmq.spring.boot;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(RocketmqEventHandlerDefinitionProperties.PREFIX)
public class RocketmqEventHandlerDefinitionProperties {
	
	public static final String PREFIX = "spring.rocketmq.consumer.event";

	private String definitions = null;

    private Map<String /* rule */, String /* handler names */> definitionMap = new LinkedHashMap<String, String>();

	public String getDefinitions() {
		return definitions;
	}

	public void setDefinitions(String definitions) {
		this.definitions = definitions;
	}

	public Map<String, String> getDefinitionMap() {
		return definitionMap;
	}

	public void setDefinitionMap(Map<String, String> definitionMap) {
		this.definitionMap = definitionMap;
	}

	
    
    
}
