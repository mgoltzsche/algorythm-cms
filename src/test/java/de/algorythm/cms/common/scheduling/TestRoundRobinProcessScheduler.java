package de.algorythm.cms.common.scheduling;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.scheduling.impl.RoundRobinProcessScheduler2;
import static org.junit.Assert.*;

public class TestRoundRobinProcessScheduler {

	static private final Logger log = LoggerFactory.getLogger(TestRoundRobinProcessScheduler.class);
	
	private class ProcessMock implements IProcess {

		public int runCount;
		
		@Override
		public void runProcess(IProcessObserver observer) {
			increaseRunCount();
		}
		
		private synchronized void increaseRunCount() {
			runCount++;
		}
	}
	
	@Test
	public void testRoundRobinProcessScheduler() throws Exception {
		final int workerCount = Runtime.getRuntime().availableProcessors();
		final RoundRobinProcessScheduler2 testee = new RoundRobinProcessScheduler2(workerCount);
		final ProcessMock[] processMocks = new ProcessMock[workerCount * 2];
		
		for (int i = 0; i < processMocks.length; i++) {
			processMocks[i] = new ProcessMock();
			testee.execute(processMocks[i]);
		}
		
		Thread.sleep(100);
		testee.shutdown();
		
		for (int i = 0; i < processMocks.length; i++) {
			int runCount = processMocks[i].runCount;
			
			assertTrue("Process run count > 1000", runCount > 100);
			
			log.info(String.format("Process %d has been run %d times", i, runCount));
		}
	}
	
	/*private IProcess createProcessMock() {
		new SimpleProcess(Arrays.asList(new ProcessMock[]{new ProcessMock(1), new ProcessMock(2)}));
	}*/
}
