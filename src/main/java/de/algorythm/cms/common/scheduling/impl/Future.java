package de.algorythm.cms.common.scheduling.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.algorythm.cms.common.scheduling.IFuture;
import de.algorythm.cms.common.scheduling.IProgressObserver;

public class Future implements IFuture, IProgressObserver {

	static private interface IFutureState {
		<V> void awaitAvailability(final Future future) throws InterruptedException;
	}
	
	static private final IFutureState AWAITING = new IFutureState() {
		@Override
		public <V> void awaitAvailability(Future future) throws InterruptedException {
			future.lock.lockInterruptibly();
			
			try {
				future.condition.await();
			} finally {
				future.lock.unlock();
			}
		}
	};
	static private final IFutureState READY = new IFutureState() {
		@Override
		public <V> void awaitAvailability(Future future) throws InterruptedException {
		}
	};
	
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private IFutureState strategy = AWAITING;
	
	@Override
	public void ready() {
		lock.lock();
		
		try {
			strategy = READY;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void sync() throws InterruptedException {
		strategy.awaitAvailability(this);
	}

}
