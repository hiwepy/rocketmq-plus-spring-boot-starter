package org.apache.rocketmq.spring.boot.handler.chain.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;

import org.apache.rocketmq.spring.boot.event.RocketmqEvent;
import org.apache.rocketmq.spring.boot.handler.EventHandler;
import org.apache.rocketmq.spring.boot.handler.NamedHandlerList;
import org.apache.rocketmq.spring.boot.handler.chain.HandlerChain;
import org.apache.rocketmq.spring.boot.handler.chain.ProxiedHandlerChain;

/**
 * Default {@link NamedHandlerList} implementation backed by an
 * {@link ArrayList}, producing {@link ProxiedHandlerChain} instances when
 * proxied.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DefaultNamedHandlerList implements NamedHandlerList<RocketmqEvent> {

	private String name;
	
	private List<EventHandler<RocketmqEvent>> backingList;
	
	public DefaultNamedHandlerList(String name) {
		 this(name, new ArrayList<EventHandler<RocketmqEvent>>());
	}

	public DefaultNamedHandlerList(String name, List<EventHandler<RocketmqEvent>> backingList) {
		 if (backingList == null) {
	            throw new NullPointerException("backingList constructor argument cannot be null.");
        }
        this.backingList = backingList;
        setName(name);
	}

    /**
     * <p>Sets the name.</p>
     * @param name
     */
	public void setName(String name) {
		 if (StringUtils.isBlank(name)) {
	         throw new IllegalArgumentException("Cannot specify a null or empty name.");
        }
        this.name = name;
	}

	@Override
    /**
     * <p>Returns the name.</p>
     * @return the get name
     */
	public String getName() {
		return this.name;
	}
	
	@Override
    /**
     * <p>Proxy.</p>
     * @param handlerChain
     * @return the proxy
     */
	public HandlerChain<RocketmqEvent> proxy(HandlerChain<RocketmqEvent> handlerChain) {
		return new ProxiedHandlerChain((ProxiedHandlerChain) handlerChain, this);
	}
	
	@Override
    /**
     * <p>Size.</p>
     * @return the size
     */
	public int size() {
		return this.backingList.size();
	}

	@Override
    /**
     * <p>Checks if empty.</p>
     * @return the is empty
     */
	public boolean isEmpty() {
		return this.backingList.isEmpty();
	}

	@Override
    /**
     * <p>Contains.</p>
     * @param o
     * @return the contains
     */
	public boolean contains(Object o) {
		return this.backingList.contains(o);
	}

	@Override
	public Iterator<EventHandler<RocketmqEvent>> iterator() {
		return this.backingList.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.backingList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.backingList.toArray(a);
	}

	@Override
    /**
     * <p>Add.</p>
     * @param e
     * @return the add
     */
	public boolean add(EventHandler<RocketmqEvent> e) {
		return this.backingList.add(e);
	}

	@Override
    /**
     * <p>Remove.</p>
     * @param o
     * @return the remove
     */
	public boolean remove(Object o) {
		return this.backingList.remove(o);
	}

	@Override
    /**
     * <p>Contains all.</p>
     * @param c
     * @return the contains all
     */
	public boolean containsAll(Collection<?> c) {
		return this.backingList.containsAll(c);
	}

	@Override
    /**
     * <p>Add all.</p>
     * @param c
     * @return the add all
     */
	public boolean addAll(Collection<? extends EventHandler<RocketmqEvent>> c) {
		return this.backingList.addAll(c);
	}

	@Override
    /**
     * <p>Add all.</p>
     * @param index
     * @param c
     * @return the add all
     */
	public boolean addAll(int index, Collection<? extends EventHandler<RocketmqEvent>> c) {
		return this.backingList.addAll(index, c);
	}

	@Override
    /**
     * <p>Remove all.</p>
     * @param c
     * @return the remove all
     */
	public boolean removeAll(Collection<?> c) {
		return this.backingList.removeAll(c);
	}

	@Override
    /**
     * <p>Retain all.</p>
     * @param c
     * @return the retain all
     */
	public boolean retainAll(Collection<?> c) {
		return this.backingList.retainAll(c);
	}

	@Override
    /**
     * <p>Clear.</p>
     */
	public void clear() {
		this.backingList.clear();
	}

	@Override
    /**
     * <p>Returns the get.</p>
     * @param index
     * @return the get
     */
	public EventHandler<RocketmqEvent> get(int index) {
		return this.backingList.get(index);
	}

	@Override
    /**
     * <p>Sets the set.</p>
     * @param index
     * @param element
     * @return the set
     */
	public EventHandler<RocketmqEvent> set(int index, EventHandler<RocketmqEvent> element) {
		return this.backingList.set(index, element);
	}

	@Override
    /**
     * <p>Add.</p>
     * @param index
     * @param element
     */
	public void add(int index, EventHandler<RocketmqEvent> element) {
		this.backingList.add(index, element);
	}

	@Override
    /**
     * <p>Remove.</p>
     * @param index
     * @return the remove
     */
	public EventHandler<RocketmqEvent> remove(int index) {
		return this.backingList.remove(index);
	}

	@Override
    /**
     * <p>Index of.</p>
     * @param o
     * @return the index of
     */
	public int indexOf(Object o) {
		return this.backingList.indexOf(o);
	}

	@Override
    /**
     * <p>Last index of.</p>
     * @param o
     * @return the last index of
     */
	public int lastIndexOf(Object o) {
		return this.backingList.lastIndexOf(o);
	}

	@Override
	public ListIterator<EventHandler<RocketmqEvent>> listIterator() {
		return this.backingList.listIterator();
	}

	@Override
	public ListIterator<EventHandler<RocketmqEvent>> listIterator(int index) {
		return this.backingList.listIterator(index);
	}

	@Override
	public List<EventHandler<RocketmqEvent>> subList(int fromIndex, int toIndex) {
		return this.backingList.subList(fromIndex, toIndex);
	}

 }
