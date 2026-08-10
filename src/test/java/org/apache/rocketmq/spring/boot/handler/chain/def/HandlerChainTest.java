package org.apache.rocketmq.spring.boot.handler.chain.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.NamedHandlerList;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for {@link DefaultHandlerChainManager}, {@link DefaultNamedHandlerList},
 * {@link ProxiedHandlerChain}, and {@link PathMatchingHandlerChainResolver}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class HandlerChainTest {

    private RocketmqEvent createEvent(String routeExpression) throws Exception {
        MessageExt msg = new MessageExt();
        msg.setTopic("test");
        msg.setTags("tag");
        msg.setKeys("key");
        msg.setBody(new byte[0]);
        RocketmqEvent event = new RocketmqEvent(msg, new MessageQueue());
        event.setRouteExpression(routeExpression);
        return event;
    }

    // ---- DefaultHandlerChainManager ----

    @Test
    void defaultManager_constructor_createsEmptyMaps() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertThat(manager.getHandlers()).isEmpty();
        assertThat(manager.getHandlerChains()).isEmpty();
        assertThat(manager.hasChains()).isFalse();
        assertThat(manager.getChainNames()).isEmpty();
    }

    @Test
    void addHandler_registersHandler() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("testHandler", handler);

        assertThat(manager.getHandler("testHandler")).isEqualTo(handler);
        assertThat(manager.getHandlers()).containsKey("testHandler");
    }

    @Test
    void addHandler_overwritesExisting() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        EventHandler<RocketmqEvent> handler1 = (event, chain) -> {};
        EventHandler<RocketmqEvent> handler2 = (event, chain) -> {};
        manager.addHandler("testHandler", handler1);
        manager.addHandler("testHandler", handler2);

        assertThat(manager.getHandler("testHandler")).isEqualTo(handler2);
    }

    @Test
    void createChain_registersChain() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("h1", handler);
        manager.createChain("myChain", "h1");

        assertThat(manager.hasChains()).isTrue();
        assertThat(manager.getChainNames()).contains("myChain");
        assertThat(manager.getChain("myChain")).isNotNull();
    }

    @Test
    void createChain_nullChainName_throwsException() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertThatThrownBy(() -> manager.createChain(null, "h1"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createChain_blankChainName_throwsException() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertThatThrownBy(() -> manager.createChain("  ", "h1"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createChain_nullDefinition_throwsException() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertThatThrownBy(() -> manager.createChain("chain", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addToChain_nullChainName_throwsException() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertThatThrownBy(() -> manager.addToChain(null, "h1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addToChain_unknownHandler_throwsException() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertThatThrownBy(() -> manager.addToChain("chain", "unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void proxy_existingChain_returnsProxied() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("h1", handler);
        manager.createChain("myChain", "h1");

        ProxiedHandlerChain original = new ProxiedHandlerChain();
        HandlerChain<RocketmqEvent> proxied = manager.proxy(original, "myChain");
        assertThat(proxied).isNotNull();
    }

    @Test
    void proxy_unknownChain_throwsException() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        ProxiedHandlerChain original = new ProxiedHandlerChain();
        assertThatThrownBy(() -> manager.proxy(original, "unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void setHandlers_updatesHandlers() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        java.util.Map<String, EventHandler<RocketmqEvent>> handlers = new java.util.LinkedHashMap<>();
        handlers.put("h1", (event, chain) -> {});
        manager.setHandlers(handlers);
        assertThat(manager.getHandlers()).hasSize(1);
    }

    @Test
    void setHandlerChains_updatesChains() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        java.util.Map<String, NamedHandlerList<RocketmqEvent>> chains = new java.util.LinkedHashMap<>();
        chains.put("c1", new DefaultNamedHandlerList("c1"));
        manager.setHandlerChains(chains);
        assertThat(manager.getHandlerChains()).hasSize(1);
    }

    // ---- DefaultNamedHandlerList ----

    @Test
    void namedHandlerList_constructor_setsName() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        assertThat(list.getName()).isEqualTo("myList");
    }

    @Test
    void namedHandlerList_nullName_throwsException() {
        assertThatThrownBy(() -> new DefaultNamedHandlerList(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void namedHandlerList_blankName_throwsException() {
        assertThatThrownBy(() -> new DefaultNamedHandlerList("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void namedHandlerList_nullBackingList_throwsException() {
        assertThatThrownBy(() -> new DefaultNamedHandlerList("name", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void namedHandlerList_addAndSize() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.isEmpty()).isFalse();
    }

    @Test
    void namedHandlerList_contains() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertThat(list.contains(handler)).isTrue();
    }

    @Test
    void namedHandlerList_remove() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        list.remove(handler);
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void namedHandlerList_getAndSet() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler1 = (event, chain) -> {};
        EventHandler<RocketmqEvent> handler2 = (event, chain) -> {};
        list.add(handler1);
        list.set(0, handler2);
        assertThat(list.get(0)).isEqualTo(handler2);
    }

    @Test
    void namedHandlerList_indexOf() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertThat(list.indexOf(handler)).isZero();
    }

    @Test
    void namedHandlerList_lastIndexOf() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertThat(list.lastIndexOf(handler)).isZero();
    }

    @Test
    void namedHandlerList_clear() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        list.add((event, chain) -> {});
        list.clear();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void namedHandlerList_addAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        java.util.List<EventHandler<RocketmqEvent>> handlers = new java.util.ArrayList<>();
        handlers.add((event, chain) -> {});
        handlers.add((event, chain) -> {});
        list.addAll(handlers);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void namedHandlerList_toArray() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        Object[] arr = list.toArray();
        assertThat(arr).hasSize(1);
    }

    @Test
    void namedHandlerList_removeByIndex() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        list.add((event, chain) -> {});
        list.remove(0);
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void namedHandlerList_addAtIndex() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler1 = (event, chain) -> {};
        EventHandler<RocketmqEvent> handler2 = (event, chain) -> {};
        list.add(handler1);
        list.add(0, handler2);
        assertThat(list.get(0)).isEqualTo(handler2);
    }

    @Test
    void namedHandlerList_addAllAtIndex() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        list.add((event, chain) -> {});
        java.util.List<EventHandler<RocketmqEvent>> handlers = new java.util.ArrayList<>();
        handlers.add((event, chain) -> {});
        list.addAll(0, handlers);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void namedHandlerList_subList() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        list.add((event, chain) -> {});
        list.add((event, chain) -> {});
        assertThat(list.subList(0, 1)).hasSize(1);
    }

    @Test
    void namedHandlerList_listIterator() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        list.add((event, chain) -> {});
        assertThat(list.listIterator()).hasNext();
        assertThat(list.listIterator(0)).hasNext();
    }

    @Test
    void namedHandlerList_containsAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertThat(list.containsAll(java.util.Collections.singletonList(handler))).isTrue();
    }

    @Test
    void namedHandlerList_removeAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        list.removeAll(java.util.Collections.singletonList(handler));
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void namedHandlerList_retainAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("myList");
        EventHandler<RocketmqEvent> handler1 = (event, chain) -> {};
        EventHandler<RocketmqEvent> handler2 = (event, chain) -> {};
        list.add(handler1);
        list.add(handler2);
        list.retainAll(java.util.Collections.singletonList(handler1));
        assertThat(list.size()).isEqualTo(1);
    }

    // ---- ProxiedHandlerChain ----

    @Test
    void proxiedChain_constructor_createsRootChain() {
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        assertThat(chain).isNotNull();
    }

    @Test
    void proxiedChain_nullOriginal_throwsException() {
        assertThatThrownBy(() -> new ProxiedHandlerChain(null, new java.util.ArrayList<>()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void proxiedChain_doHandler_executesHandlers() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        java.util.List<EventHandler<RocketmqEvent>> handlers = new java.util.ArrayList<>();
        handlers.add((event, chain) -> {
            counter.incrementAndGet();
            chain.doHandler(event);
        });

        ProxiedHandlerChain root = new ProxiedHandlerChain();
        ProxiedHandlerChain chain = new ProxiedHandlerChain(root, handlers);

        RocketmqEvent event = createEvent("/test/path");
        chain.doHandler(event);

        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    void proxiedChain_doHandler_noHandlers_delegatesToOriginal() throws Exception {
        ProxiedHandlerChain root = new ProxiedHandlerChain();
        ProxiedHandlerChain chain = new ProxiedHandlerChain(root, new java.util.ArrayList<>());

        RocketmqEvent event = createEvent("/test/path");
        // Should not throw
        chain.doHandler(event);
    }

    @Test
    void proxiedChain_doHandler_rootChain_noOp() throws Exception {
        ProxiedHandlerChain root = new ProxiedHandlerChain();
        RocketmqEvent event = createEvent("/test/path");
        // Should not throw - root chain has no handlers
        root.doHandler(event);
    }

    // ---- PathMatchingHandlerChainResolver ----

    @Test
    void pathResolver_constructor_createsDefaults() {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        assertThat(resolver.getHandlerChainManager()).isNotNull();
        assertThat(resolver.getPathMatcher()).isNotNull();
    }

    @Test
    void pathResolver_getChain_noChains_returnsNull() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        RocketmqEvent event = createEvent("/test/path");
        assertThat(resolver.getChain(event, new ProxiedHandlerChain())).isNull();
    }

    @Test
    void pathResolver_getChain_matchingPath_returnsChain() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        DefaultHandlerChainManager manager = (DefaultHandlerChainManager) resolver.getHandlerChainManager();

        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("h1", handler);
        manager.createChain("/test/**", "h1");

        RocketmqEvent event = createEvent("/test/path");
        HandlerChain<RocketmqEvent> chain = resolver.getChain(event, new ProxiedHandlerChain());
        assertThat(chain).isNotNull();
    }

    @Test
    void pathResolver_getChain_noMatchingPath_returnsNull() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        DefaultHandlerChainManager manager = (DefaultHandlerChainManager) resolver.getHandlerChainManager();

        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("h1", handler);
        manager.createChain("/other/**", "h1");

        RocketmqEvent event = createEvent("/test/path");
        assertThat(resolver.getChain(event, new ProxiedHandlerChain())).isNull();
    }

    @Test
    void pathResolver_setHandlerChainManager_updatesManager() {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        DefaultHandlerChainManager newManager = new DefaultHandlerChainManager();
        resolver.setHandlerChainManager(newManager);
        assertThat(resolver.getHandlerChainManager()).isEqualTo(newManager);
    }

    @Test
    void pathResolver_setPathMatcher_updatesMatcher() {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        org.springframework.util.PathMatcher matcher = new org.springframework.util.AntPathMatcher();
        resolver.setPathMatcher(matcher);
        assertThat(resolver.getPathMatcher()).isEqualTo(matcher);
    }
}
