package de.algorythm.cms.common.scheduling;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.scheduling.impl.RoundRobinProcessScheduler;
import static org.junit.Assert.*;

public class TestRoundRobinProcessScheduler {

	static private final Logger log = LoggerFactory.getLogger(TestRoundRobinProcessScheduler.class);
	
	private volatile boolean concurrent = false;
	
	private class ProcessMock implements IProcess {

		public volatile int runCount;
		
		@Override
		public void runProcess(IProcessObserver observer) {
			final int increasedRunCount = increaseRunCount();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
			if (runCount != increasedRunCount)
				concurrent = true;
		}
		
		private synchronized int increaseRunCount() {
			return ++runCount;
		}
	}
	
	@Test
	public void testRoundRobinProcessScheduler() throws Exception {
		final int workerCount = Math.max(2, Runtime.getRuntime().availableProcessors());
		final RoundRobinProcessScheduler testee = new RoundRobinProcessScheduler(workerCount);
		final ProcessMock[] processMocks = new ProcessMock[workerCount * 2];
		
		for (int i = 0; i < processMocks.length; i++) {
			processMocks[i] = new ProcessMock();
			testee.execute(processMocks[i]);
		}
		
		Thread.sleep(100);
		testee.shutdown();
		
		for (int i = 0; i < processMocks.length; i++) {
			int runCount = processMocks[i].runCount;
			
			assertTrue("Process run count > 10", runCount > 10);
			
			log.info(String.format("Process %d has been run %d times", i, runCount));
		}
		
		assertTrue("IProcess.run() should be executed concurrently", concurrent);
	}
	
	/*private IProcess createProcessMock() {
		new SimpleProcess(Arrays.asList(new ProcessMock[]{new ProcessMock(1), new ProcessMock(2)}));
	}*/
}
