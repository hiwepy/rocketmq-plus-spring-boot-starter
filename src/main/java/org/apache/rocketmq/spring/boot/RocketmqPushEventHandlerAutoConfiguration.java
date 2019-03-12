package org.apache.rocketmq.spring.boot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.boot.annotation.RocketmqPushConsumer;
import org.apache.rocketmq.spring.boot.config.Ini;
import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.Nameable;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.def.DefaultHandlerChainManager;
import org.apache.rocketmq.spring.boot.handler.chain.def.PathMatchingHandlerChainResolver;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageConcurrentlyHandler;
import org.apache.rocketmq.spring.boot.handler.impl.RocketmqEventMessageOrderlyHandler;
import org.apache.rocketmq.spring.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@ConditionalOnProperty(prefix = RocketmqPushConsumerProperties.PREFIX, value = "enabled", havingValue = "true")
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
@EnableConfigurationProperties({ RocketmqPushEventHandlerDefinitionProperties.class })
public class RocketmqPushEventHandlerAutoConfiguration implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqPushEventHandlerAutoConfiguration.class);
	private ApplicationContext applicationContext;
	
	/**
	 * 处理器链定义
	 */
	private Map<String, String> handlerChainDefinitionMap = new HashMap<String, String>();
	
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
				if (entry.getValue() instanceof RocketmqEventMessageConcurrentlyHandler ||
						entry.getValue() instanceof RocketmqEventMessageOrderlyHandler) {
					//跳过入口实现类
					continue;
				}
				RocketmqPushConsumer annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), RocketmqPushConsumer.class);
				if(annotationType == null) {
					// 注解为空，则打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", RocketmqPushConsumer.class, entry.getValue().getClass(), entry.getKey());
				} else {
					
					String rule = new StringBuilder("/").append(annotationType.topic()).append("/").append(annotationType.tags()).append("/")
					.append(annotationType.keys()).toString();
					
					handlerChainDefinitionMap.put(rule, entry.getKey());
				}
				
				rocketmqEventHandlers.put(entry.getKey(), entry.getValue());
			}
		}
		// BeanFactoryUtils.beansOfTypeIncludingAncestors(getApplicationContext(), EventHandler.class);

		return rocketmqEventHandlers;
	}
	
	@Bean
	public RocketmqEventMessageConcurrentlyHandler messageConcurrentlyHandler(
			RocketmqPushEventHandlerDefinitionProperties properties,
			@Qualifier("rocketmqEventHandlers") Map<String, EventHandler<RocketmqEvent>> eventHandlers) {
		
		if( StringUtils.isNotEmpty(properties.getDefinitions())) {
			this.setHandlerChainDefinitions(properties.getDefinitions());
		} else if (!CollectionUtils.isEmpty(properties.getDefinitionMap())) {
			getHandlerChainDefinitionMap().putAll(properties.getDefinitionMap());
		}
		
		HandlerChainManager<RocketmqEvent> manager = createHandlerChainManager(eventHandlers);
        PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
        chainResolver.setHandlerChainManager(manager);
        return new RocketmqEventMessageConcurrentlyHandler(chainResolver);
	}
	
	@Bean
	public RocketmqEventMessageOrderlyHandler messageOrderlyHandler(
			RocketmqPushEventHandlerDefinitionProperties properties,
			@Qualifier("rocketmqEventHandlers") Map<String, EventHandler<RocketmqEvent>> eventHandlers) {
		
		if( StringUtils.isNotEmpty(properties.getDefinitions())) {
			this.setHandlerChainDefinitions(properties.getDefinitions());
		} else if (!CollectionUtils.isEmpty(properties.getDefinitionMap())) {
			getHandlerChainDefinitionMap().putAll(properties.getDefinitionMap());
		}
		
		HandlerChainManager<RocketmqEvent> manager = createHandlerChainManager(eventHandlers);
        PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
        chainResolver.setHandlerChainManager(manager);
        return new RocketmqEventMessageOrderlyHandler(chainResolver);
	}
	
	protected void setHandlerChainDefinitions(String definitions) {
        Ini ini = new Ini();
        ini.load(definitions);
        Ini.Section section = ini.getSection("urls");
        if (CollectionUtils.isEmpty(section)) {
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }
        getHandlerChainDefinitionMap().putAll(section);
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
