package org.apache.rocketmq.spring.boot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.boot.config.Ini;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.Nameable;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.def.DefaultHandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.def.PathMatchingHandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageHandler;
import org.apache.rocketmq.spring.boot.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Configuration
@ConditionalOnClass({ DefaultMQPushConsumer.class })
@ConditionalOnProperty(name = RocketmqEventHandlerDefinitionProperties.PREFIX, matchIfMissing = true)
@EnableConfigurationProperties({ RocketmqEventHandlerDefinitionProperties.class })
public class RocketmqEventHandlerAutoConfiguration implements ApplicationContextAware {


	private ApplicationContext applicationContext;
	
	/**
	 * 处理器链定义
	 */
	private Map<String, String> handlerChainDefinitionMap;
	
	/**
	 * 处理器定义
	 */
	@Bean("rocketmqEventHandlers")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, EventHandler<RocketmqEvent>> rocketmqEventHandlers() {

		Map<String, EventHandler<RocketmqEvent>> rocketmqEventHandlers = new LinkedHashMap<String, EventHandler<RocketmqEvent>>();

		Map<String, EventHandler> beansOfType = getApplicationContext().getBeansOfType(EventHandler.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, EventHandler>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, EventHandler> entry = ite.next();
				if (entry.getValue() instanceof RocketmqEventMessageHandler ) {
					//跳过入口实现类
					continue;
				}
				rocketmqEventHandlers.put(entry.getKey(), entry.getValue());
			}
		}
		// BeanFactoryUtils.beansOfTypeIncludingAncestors(getApplicationContext(), EventHandler.class);

		return rocketmqEventHandlers;
	}
	
	@Bean
	public RocketmqEventMessageHandler rocketmqEventMessageHandler(
			RocketmqEventHandlerDefinitionProperties properties,
			@Qualifier("rocketmqEventHandlers") Map<String, EventHandler<RocketmqEvent>> eventHandlers) {
		
		if( StringUtils.isNotEmpty(properties.getDefinitions())) {
			this.setHandlerChainDefinitions(properties.getDefinitions());
		} else if (!CollectionUtils.isEmpty(properties.getDefinitionMap())) {
			this.setHandlerChainDefinitionMap(properties.getDefinitionMap());
		}
		
		HandlerChainManager<RocketmqEvent> manager = createHandlerChainManager(eventHandlers);
        PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
        chainResolver.setHandlerChainManager(manager);
        return new RocketmqEventMessageHandler(chainResolver);
	}
	
	protected void setHandlerChainDefinitions(String definitions) {
        Ini ini = new Ini();
        ini.load(definitions);
        Ini.Section section = ini.getSection("urls");
        if (CollectionUtils.isEmpty(section)) {
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }
        setHandlerChainDefinitionMap(section);
    }
	
	protected HandlerChainManager<RocketmqEvent> createHandlerChainManager(
			Map<String, EventHandler<RocketmqEvent>> eventHandlers) {

		HandlerChainManager<RocketmqEvent> manager = new DefaultHandlerChainManager();
		if (!CollectionUtils.isEmpty(eventHandlers)) {
			for (Map.Entry<String, EventHandler<RocketmqEvent>> entry : eventHandlers.entrySet()) {
				String name = entry.getKey();
				EventHandler<RocketmqEvent> handler = entry.getValue();
				if (handler instanceof Nameable) {
					((Nameable) handler).setName(name);
				}
				manager.addHandler(name, handler);
			}
		}

		Map<String, String> chains = getHandlerChainDefinitionMap();
		if (!CollectionUtils.isEmpty(chains)) {
			for (Map.Entry<String, String> entry : chains.entrySet()) {
				// topic/tags/keys
				String rule = entry.getKey();
				String chainDefinition = entry.getValue();
				manager.createChain(rule, chainDefinition);
			}
		}
		
		return manager;
	}


	public Map<String, String> getHandlerChainDefinitionMap() {
		return handlerChainDefinitionMap;
	}


	public void setHandlerChainDefinitionMap(Map<String, String> handlerChainDefinitionMap) {
		this.handlerChainDefinitionMap = handlerChainDefinitionMap;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
