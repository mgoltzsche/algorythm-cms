package de.algorythm.cms.common.scheduling.impl;

import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.cms.common.scheduling.IProcess;
import de.algorythm.cms.common.scheduling.IProcessObserver;

public class SimpleProcess implements IProcess {

	private final LinkedList<Runnable> jobs;

	public SimpleProcess(final Collection<Runnable> jobs) {
		this.jobs = new LinkedList<Runnable>(jobs);
	}
	
	@Override
	public void runProcess(final IProcessObserver processObserver) {
		final Runnable nextJob = nextJob();
		
		if (nextJob == null)
			processObserver.terminateProcess();
		else
			nextJob.run();
	}

	private synchronized Runnable nextJob() {
		return jobs.poll();
	}
}
