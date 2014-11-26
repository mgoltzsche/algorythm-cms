package de.algorythm.cms.common.scheduling.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.scheduling.IProcess;
import de.algorythm.cms.common.scheduling.IProcessObserver;
import de.algorythm.cms.common.scheduling.impl.BlockingRingBuffer.INode;

public class RoundRobinProcessScheduler2 {

	static private final Logger log = LoggerFactory.getLogger(RoundRobinProcessScheduler2.class);
	
	private class Worker extends Thread implements IProcessObserver {

		private final Lock workerLock = new ReentrantLock();
		private INode<IProcess> currentProcessNode;
		
		public Worker(final ThreadGroup threadGroup, final String name) {
			super(threadGroup, name);
		}

		@Override
		public void run() {
			while (execute) {
				try {
					currentProcessNode = processes.next();
				} catch(InterruptedException e) {
					if (execute)
						log.error("Worker thread interrupted", e);
					else
						return;
				}
				
				workerLock.lock();
				
				try {
					currentProcessNode.getValue().runProcess(this);
				} catch (Throwable e) {
					log.error("Process '" + currentProcessNode.getValue() + "' threw " + e.getClass() + ": " + e.getMessage());
				} finally {
					workerLock.unlock();
				}
			}
		}

		@Override
		public void terminateProcess() {
			currentProcessNode.remove();
		}
		
		public boolean shutdown() {
			if (workerLock.tryLock()) {
				try {
					interrupt();
				} finally {
					workerLock.unlock();
				}
				
				return true;
			} else {
				return false;
			}
		}
	}

	private final LinkedList<Worker> workers = new LinkedList<Worker>();
	private final BlockingRingBuffer<IProcess> processes = new BlockingRingBuffer<IProcess>();
	private boolean execute = true;
	
	public RoundRobinProcessScheduler2() {
		this(Runtime.getRuntime().availableProcessors());
	}
	
	public RoundRobinProcessScheduler2(final int workerCount) {
		final ThreadGroup threadGroup = new ThreadGroup("scheduler-workers");
		
		for (int i = 0; i < workerCount; i++) {
			final String workerName = "scheduler-worker-" + i;
			final Worker worker = new Worker(threadGroup, workerName);
			
			worker.start();
			workers.add(worker);
		}
	}
	
	public void addProcess(final IProcess process) {
		processes.add(process);
	}
	
	public void shutdown() {
		execute = false;
		
		final LinkedList<Worker> activeWorkers = new LinkedList<Worker>(workers);
		
		while (!activeWorkers.isEmpty()) {
			final Iterator<Worker> activeWorkerIter = activeWorkers.iterator();
			
			while (activeWorkerIter.hasNext()) {
				final Worker worker = activeWorkerIter.next();
				
				if (worker.shutdown())
					activeWorkerIter.remove();
			}
		}
	}
}
