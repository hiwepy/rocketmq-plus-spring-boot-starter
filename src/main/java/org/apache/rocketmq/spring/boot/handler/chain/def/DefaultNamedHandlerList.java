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

	public void setName(String name) {
		 if (StringUtils.isBlank(name)) {
	         throw new IllegalArgumentException("Cannot specify a null or empty name.");
        }
        this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public HandlerChain<RocketmqEvent> proxy(HandlerChain<RocketmqEvent> handlerChain) {
		return new ProxiedHandlerChain((ProxiedHandlerChain) handlerChain, this);
	}
	
	@Override
	public int size() {
		return this.backingList.size();
	}

	@Override
	public boolean isEmpty() {
		return this.backingList.isEmpty();
	}

	@Override
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
	public boolean add(EventHandler<RocketmqEvent> e) {
		return this.backingList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return this.backingList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.backingList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends EventHandler<RocketmqEvent>> c) {
		return this.backingList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends EventHandler<RocketmqEvent>> c) {
		return this.backingList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.backingList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.backingList.retainAll(c);
	}

	@Override
	public void clear() {
		this.backingList.clear();
	}

	@Override
	public EventHandler<RocketmqEvent> get(int index) {
		return this.backingList.get(index);
	}

	@Override
	public EventHandler<RocketmqEvent> set(int index, EventHandler<RocketmqEvent> element) {
		return this.backingList.set(index, element);
	}

	@Override
	public void add(int index, EventHandler<RocketmqEvent> element) {
		this.backingList.add(index, element);
	}

	@Override
	public EventHandler<RocketmqEvent> remove(int index) {
		return this.backingList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.backingList.indexOf(o);
	}

	@Override
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
