package org.apache.rocketmq.spring.boot.handler.chain;

import java.util.Map;
import java.util.Set;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.NamedHandlerList;

/**
 * Manager responsible for creating and maintaining {@link HandlerChain}s and
 * the registry of named {@link EventHandler}s.
 *
 * @param <T> the event type, bound to {@link RocketmqEvent}
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface HandlerChainManager<T extends RocketmqEvent> {

	/**
	 * @return all registered handlers keyed by name
	 */
    Map<String, EventHandler<T>> getHandlers();

	/**
	 * @return all registered handler chains keyed by name
	 */
    Map<String, NamedHandlerList<T>> getHandlerChains();

	/**
	 * Returns the named handler list for the given chain name.
	 *
	 * @param chainName the chain name
	 * @return the matching handler list, or {@code null}
	 */
    NamedHandlerList<T> getChain(String chainName);

	/**
	 * @return {@code true} if at least one handler chain is registered
	 */
    boolean hasChains();

	/**
	 * @return the set of registered chain names
	 */
    Set<String> getChainNames();

	/**
	 * Builds a proxy chain that first executes the named chain and finally
	 * delegates to the supplied original chain.
	 *
	 * @param original  the original (root) chain
	 * @param chainName the name of the chain to execute first
	 * @return the proxied handler chain
	 */
    HandlerChain<T> proxy(HandlerChain<T> original, String chainName);

   /**
    * Registers a handler under the given name.
    *
    * @param name    the handler name
    * @param handler the handler to register
    */
    void addHandler(String name, EventHandler<T> handler);

    /**
     * Creates a new handler chain bound to the given name and definition.
     *
     * @param chainName       the chain name
     * @param chainDefinition the handler names that make up the chain
     */
    void createChain(String chainName, String chainDefinition);

    /**
     * Appends a handler to an existing chain.
     *
     * @param chainName   the chain to append to
     * @param handlerName the handler name to append
     */
    void addToChain(String chainName, String handlerName);

}