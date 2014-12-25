package de.algorythm.cms.common.scheduling.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingCycle<V> {

	static public interface INode<V> {
		V getValue();
		void remove();
	}
	
	static private interface INodeRemoveStrategy<V> {
		void remove(Node<V> node);
	}
	
	static private class Node<V> implements INode<V> {
		public final V value;
		protected Node<V> previous;
		protected Node<V> next;
		private INodeRemoveStrategy<V> removeStrategy;
		
		public Node(final V value, final INodeRemoveStrategy<V> removeStrategy) {
			this.value = value;
			this.removeStrategy = removeStrategy;
		}
		
		@Override
		public V getValue() {
			return value;
		}
		
		public Node<V> next() throws InterruptedException {
			return next.resolve().awaitAvailability();
		}
		
		protected Node<V> resolve() {
			return this;
		}
		
		protected Node<V> awaitAvailability() throws InterruptedException {
			return this;
		}
		
		public void add(final V nextValue) {
			final Node<V> nextNode = new Node<V>(nextValue, removeStrategy);
			nextNode.next = next;
			nextNode.previous = this;
			nextNode.next.previous = nextNode;
			next = nextNode;
		}
		
		public void remove() {
			removeStrategy.remove(this);
		}
	}
	
	static private class NullNode<V> extends Node<V> {
		
		private final Condition condition;
		
		public NullNode(final Condition condition, final INodeRemoveStrategy<V> removeStrategy) {
			super(null, removeStrategy);
			this.condition = condition;
			previous = next = this;
		}
		
		@Override
		public Node<V> awaitAvailability() throws InterruptedException {
			condition.await();
			
			return next;
		}
		
		@Override
		protected Node<V> resolve() {
			return next;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove null node");
		}
	}
	
	private final INodeRemoveStrategy<V> REMOVABLE_NODE = new INodeRemoveStrategy<V>() {
		@Override
		public void remove(final Node<V> node) {
			lock.lock();
			
			try {
				node.next.previous = node.previous;
				node.previous.next = node.next;
				node.removeStrategy = REMOVED_NODE;
				size--;
			} finally {
				lock.unlock();
			}
		}
	};
	private final INodeRemoveStrategy<V> REMOVED_NODE = new INodeRemoveStrategy<V>() {
		@Override
		public void remove(final Node<V> node) {
			throw new IllegalStateException("Node value '" + node.value + "' has already been removed");
		}
	};
	
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private Node<V> currentNode = new NullNode<V>(condition, REMOVABLE_NODE);
	private int size;
	
	public void add(V value) {
		lock.lock();
		
		try {
			currentNode.add(value);
			size++;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public INode<V> next() throws InterruptedException {
		lock.lockInterruptibly();
		
		try {
			return currentNode = currentNode.next();
		} finally {
			lock.unlock();
		}
	}
	
	public int size() {
		lock.lock();
		
		try {
			return size;
		} finally {
			lock.unlock();
		}
	}
}
