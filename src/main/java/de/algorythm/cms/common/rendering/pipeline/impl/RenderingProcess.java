package de.algorythm.cms.common.rendering.pipeline.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.scheduling.IProcess;
import de.algorythm.cms.common.scheduling.IProcessObserver;

public class RenderingProcess implements IProcess, IRenderingContext {

	static private class PipelinePhase {
		
		public final LinkedList<Runnable> jobs;
		public int pendingSize;
		
		public PipelinePhase(final Collection<Runnable> jobs) {
			this.jobs = new LinkedList<Runnable>(jobs);
			this.pendingSize = jobs.size();
		}
	}
	
	
	private final Iterator<PipelinePhase> phaseIter;
	private PipelinePhase currentPhase;
	private final Map<String, String> properties = Collections.synchronizedMap(new HashMap<String, String>());

	public RenderingProcess(final List<Collection<Runnable>> jobPhases) {
		final LinkedList<PipelinePhase> phases = new LinkedList<PipelinePhase>();
		
		for (Collection<Runnable> jobs : jobPhases) {
			final PipelinePhase phase = new PipelinePhase(jobs);
			
			phases.add(phase);
		}
		
		phaseIter = phases.iterator();
		currentPhase = phaseIter.next();
	}

	@Override
	public void addJob(Runnable job) {
		synchronized(phaseIter) {
			currentPhase.jobs.add(job);
			currentPhase.pendingSize++;
		}
	}

	@Override
	public void runProcess(final IProcessObserver processObserver) {
		final Runnable nextJob;
		
		synchronized(phaseIter) {
			nextJob = currentPhase.jobs.poll();
		}
		
		if (nextJob != null) {
			nextJob.run();
			
			synchronized(phaseIter) {
				if (--currentPhase.pendingSize == 0) {
					if (phaseIter.hasNext())
						currentPhase = phaseIter.next();
					else
						processObserver.terminateProcess();
				}
			}
		}
	}

	@Override
	public String getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}
}
