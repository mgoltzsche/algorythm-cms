package de.algorythm.cms.common.concurrent;

public class Future<R> extends Condition {

	static private interface IStrategy {
		
		<R> R getResult(Future<R> self);
		<R> void setResult(Future<R> self, R result);
	}
	static private IStrategy RESOLVED = new IStrategy() {
		@Override
		public <R> R getResult(Future<R> self) {
			return self.result;
		}
		@Override
		public <R> void setResult(Future<R> self, R result) {
			throw new IllegalStateException("Result is already set");
		}
	};
	static private IStrategy PENDING = new IStrategy() {
		@Override
		public <R> R getResult(Future<R> self) {
			throw new IllegalStateException("Result is not yet known");
		}
		@Override
		public <R> void setResult(Future<R> self, R result) {
			self.result = result;
			self.strategy = RESOLVED;
			self.fulfill();
		}
	};
	
	private volatile R result;
	private IStrategy strategy;

	public Future() {
		strategy = PENDING;
	}

	public Future(R result) {
		strategy = RESOLVED;
		this.result = result;
		fulfill();
	}

	public R getResult() {
		return strategy.getResult(this);
	}

	public void setResult(R result) {
		strategy.setResult(this, result);
	}
}
