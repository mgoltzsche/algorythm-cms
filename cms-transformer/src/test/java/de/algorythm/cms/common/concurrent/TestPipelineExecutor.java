package de.algorythm.cms.common.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Test;

import de.algorythm.cms.common.scheduling.IExecutor;

public class TestPipelineExecutor {

	static private interface IFacadeMock {
		Future<Long> renderA(long k);
		Future<String> renderB(Future<Long> a);
		Future<String> renderC(Future<Long> a, Future<String> b);
		Future<Long> renderX(Future<Long> a, int incr);
	}
	
	private Set<String> methodCalls = new LinkedHashSet<>();
	private final IFacadeMock mock = new IFacadeMock() {
		@Override
		public Future<Long> renderA(long k) {
			methodCalls.add("renderA");
			return new Future<>(k);
		}
		@Override
		public Future<String> renderB(Future<Long> a) {
			methodCalls.add("renderB");
			return new Future<>(a.getResult() + "b");
		}
		@Override
		public Future<String> renderC(Future<Long> a, Future<String> b) {
			methodCalls.add("renderC");
			return new Future<>(a.getResult() + " " + b.getResult() + " c");
		}
		@Override
		public Future<Long> renderX(Future<Long> a, int incr) {
			methodCalls.add("renderX");
			return new Future<>(a.getResult() + incr);
		}
	};
	
	@Test
	public void createExecutor_should_invoke_calls_correctly() {
		IExecutor executor = new IExecutor() {
			@Override
			public void execute(Runnable job) {
				job.run();
			}
		};
		PipelineExecutor testee = new PipelineExecutor(executor);
		IFacadeMock mockCaller = testee.createExecutor(mock, IFacadeMock.class);
		Future<Long> a = mockCaller.renderA(10);
		Future<String> b = mockCaller.renderB(a);
		Future<String> c = mockCaller.renderC(a, b);
		
		assertEquals("c", "10 10b c", c.getResult());
	}
	
	@Test
	public void createExecutor_should_invoke_result_calls_conditionally() {
		final LinkedList<Runnable> jobs = new LinkedList<>();
		IExecutor executor = new IExecutor() {
			@Override
			public void execute(Runnable job) {
				jobs.add(job);
			}
		};
		PipelineExecutor testee = new PipelineExecutor(executor);
		IFacadeMock mockCaller = testee.createExecutor(mock, IFacadeMock.class);
		Future<Long> a = mockCaller.renderA(10);
		Future<String> b = mockCaller.renderB(a);
		Future<String> c = mockCaller.renderC(a, b);
		
		for (int i = 0; i < 7; i++) {
			mockCaller.renderX(a, i);
		}
		
		assertFutureUnavailable(a);
		assertFutureUnavailable(b);
		assertFutureUnavailable(c);
		
		assertEquals("jobs pending", 1, jobs.size());
		jobs.pop().run();
		assertTrue("renderA method called", methodCalls.contains("renderA"));
		assertEquals("jobs pending", 8, jobs.size());
		
		for (int i = 0; i < 9; i++)
			jobs.pop().run();
		
		assertTrue("renderB, renderC and renderX methods called", methodCalls.containsAll(Arrays.asList(new String[] {"renderB", "renderC", "renderX"})));
		assertEquals("jobs pending", 0, jobs.size());
		assertEquals("c", "10 10b c", c.getResult());
		assertTrue("All methods called", methodCalls.containsAll(Arrays.asList(new String[] {"renderA", "renderB", "renderC", "renderX"})));
	}
	
	private void assertFutureUnavailable(Future<?> future) {
		try {
			future.getResult();
			throw new AssertionError("Unavailable future should throw IllegalStateException on getResult() call");
		} catch(IllegalStateException e) {}
	}
}
