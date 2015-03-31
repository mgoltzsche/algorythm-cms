package de.algorythm.cms.common.scheduling.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.algorythm.cms.common.scheduling.IFuture;
import de.algorythm.cms.common.scheduling.IProgressObserver;

public class Future<R> implements IFuture<R>, IProgressObserver<R> {

	static private interface IFutureState {
		<R> R awaitAvailability(Future<R> future) throws Throwable;
	}

	static private final IFutureState AWAITING = new IFutureState() {
		@Override
		public <R> R awaitAvailability(final Future<R> future) throws Throwable {
			future.lock.lockInterruptibly();
			
			try {
				future.condition.await();
			} finally {
				future.lock.unlock();
			}
			
			return future.strategy.awaitAvailability(future);
		}
	};
	static private final IFutureState READY = new IFutureState() {
		@Override
		public <R> R awaitAvailability(final Future<R> future) throws Throwable {
			return future.result;
		}
	};
	static private final IFutureState FAILED = new IFutureState() {
		@Override
		public <R> R awaitAvailability(final Future<R> future) throws Throwable {
			throw future.error;
		}
	};

	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private IFutureState strategy = AWAITING;
	private R result;
	private Throwable error;

	public Future() {}

	public Future(R result) {
		finished(result);
	}

	@Override
	public R getResult() {
		return result;
	}

	@Override
	public void finished() {
		finished(null, null, READY);
	}

	@Override
	public void finished(final R result) {
		finished(result, null, READY);
	}

	@Override
	public void finishedWithError(final Throwable e) {
		finished(null, e, FAILED);
	}

	private void finished(final R result, final Throwable error, final IFutureState state) {
		lock.lock();
		
		try {
			this.result = result;
			this.error = error;
			this.strategy = state;
			
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public R sync() throws Throwable {
		return strategy.awaitAvailability(this);
	}
}
