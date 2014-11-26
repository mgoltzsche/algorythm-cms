/*package de.algorythm.cms.common.scheduling.impl;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.scheduling.IProcess;
import de.algorythm.cms.common.scheduling.IProcessScheduler;

public class RoundRobinProcessScheduler implements IProcessScheduler {
	
	static private final Logger log = LoggerFactory.getLogger(RoundRobinProcessScheduler.class);

	private class ProcessJobRunner implements Runnable {

		private final IProcess process;
		private final Runnable job;

		public ProcessJobRunner(final IProcess process, final Runnable job) {
			this.process = process;
			this.job = job;
		}

		@Override
		public void run() {
			try {
				try {
					job.run();
				} catch(Throwable e) {
					log.error("Job execution '" + job + "' failed. " + e.getMessage(), e);
				} finally {
					process.jobTerminated(job);
				}
			} finally {
				executeNextJobsFromProcesses();
			}
		}
	}
	
	private final ThreadPoolExecutor executor;
	private final LinkedList<IProcess> processes = new LinkedList<IProcess>();
	private final int workerCount;

	public RoundRobinProcessScheduler() {
		workerCount = Runtime.getRuntime().availableProcessors();
		executor = new ThreadPoolExecutor(workerCount, workerCount, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());
	}

	@Override
	public synchronized void executeProcess(final IProcess process) {
		processes.addFirst(process);
		executeNextJobsFromProcesses();
	}

	private synchronized void executeNextJobsFromProcesses() {
		int freeCapacity = workerCount - executor.getQueue().size();
		
		while (freeCapacity > 0 && !processes.isEmpty()) {
			final IProcess nextProcess = processes.poll();
			final Runnable nextJob = nextProcess.nextJob();
			
			if (nextJob != null) {
				executor.execute(new ProcessJobRunner(nextProcess, nextJob));
				processes.add(nextProcess);
				freeCapacity--;
			} else {
				log.info("Process terminated: " + nextProcess);
			}
		}
	}
}*/
