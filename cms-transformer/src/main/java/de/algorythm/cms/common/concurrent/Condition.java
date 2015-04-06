package de.algorythm.cms.common.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Condition implements IConditionListener {

	static private interface IStrategy {

		void addListener(Condition self, IConditionListener listener);
		void fulfill(Condition self);
		void preconditionFullfilled(Condition self, Condition precondition);
	}
	
	static private final IStrategy FULFILLED = new IStrategy() {
		
		@Override
		public void addListener(Condition self, IConditionListener listener) {
			listener.fulfilled(self);
		}
		
		@Override
		public void fulfill(Condition self) {
			throw new IllegalStateException("Condition is already fulfilled");
		}
		
		@Override
		public void preconditionFullfilled(Condition self, Condition precondition) {
			throw new IllegalStateException("Condition is already fulfilled");
		}
	};
	
	static private final IStrategy PENDING = new IStrategy() {

		@Override
		public void addListener(Condition self, IConditionListener listener) {
			self.lock.lock();
			
			try {
				self.listeners.add(listener);
			} finally {
				self.lock.unlock();
			}
		}
		
		@Override
		public void fulfill(Condition self) {
			for (IConditionListener listener : self.listeners)
				listener.fulfilled(self);
			
			self.listeners.clear();
		}

		@Override
		public void preconditionFullfilled(Condition self, Condition precondition) {
			boolean fulfilled = false;
			
			self.lock.lock();
			
			try {
				if (self.preconditions.remove(precondition) &&
						(fulfilled = self.preconditions.isEmpty())) {
					self.strategy = FULFILLED;
					self.preconditions.clear();
				}
			} finally {
				self.lock.unlock();
			}
			
			if (fulfilled)
				fulfill(self);
		}
	};

	private IStrategy strategy;
	private final Set<Condition> preconditions = new HashSet<>();
	private final Set<IConditionListener> listeners = new HashSet<>();
	private final Lock lock = new ReentrantLock();

	public Condition() {
		strategy = PENDING;
	}

	public Condition(Condition... preconditions) {
		this(Arrays.asList(preconditions));
	}

	public Condition(Collection<? extends Condition> preconditions) {
		if (preconditions.isEmpty()) {
			strategy = FULFILLED;
		} else {
			strategy = PENDING;
			
			for (Condition pre : preconditions)
				this.preconditions.add(pre);
			
			for (Condition pre : preconditions)
				pre.addListener(this);
		}
	}

	public void addListener(IConditionListener listener) {
		strategy.addListener(this, listener);
	}

	void fulfill() {
		final IStrategy s;
		
		lock.lock();
		
		try {
			s = strategy;
			strategy = FULFILLED;
		} finally {
			lock.unlock();
		}
		
		s.fulfill(this);
	}

	@Override
	public void fulfilled(Condition precondition) {
		strategy.preconditionFullfilled(this, precondition);
	}
}