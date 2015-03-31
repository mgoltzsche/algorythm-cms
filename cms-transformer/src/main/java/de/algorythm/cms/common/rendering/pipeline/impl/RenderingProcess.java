/*package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.scheduling.IProcess;
import de.algorythm.cms.common.scheduling.IProcessObserver;
import de.algorythm.cms.common.scheduling.IProgressObserver;

public class RenderingProcess implements IProcess, IRenderingContext {

	static private final Logger log = LoggerFactory.getLogger(RenderingProcess.class);
	
	static private class PipelinePhase {
		
		public final LinkedList<IRenderingJob> jobs;
		public int pendingSize;
		
		public PipelinePhase(final LinkedList<IRenderingJob> jobs) {
			this.jobs = jobs;
			this.pendingSize = jobs.size();
		}
	}

	private final IRenderingContext context;
	private final Iterator<PipelinePhase> phaseIter;
	private PipelinePhase currentPhase;
	private final IProgressObserver<Void> observer;
	private final long startTime = System.currentTimeMillis();

	public RenderingProcess(final IRenderingContext context, final List<Collection<IRenderingJob>> jobPhases, final IProgressObserver<Void> observer) {
		if (jobPhases.isEmpty())
			throw new IllegalArgumentException("No jobs to execute");
		
		this.context = context;
		this.observer = observer;
		final LinkedList<PipelinePhase> phases = new LinkedList<PipelinePhase>();
		
		for (Collection<IRenderingJob> jobs : jobPhases)
			phases.add(new PipelinePhase(new LinkedList<IRenderingJob>(jobs)));
		
		phaseIter = phases.iterator();
		currentPhase = phaseIter.next();
	}

	@Override
	public void runProcess(final IProcessObserver processObserver) {
		final IRenderingJob nextJob;
		
		synchronized(phaseIter) {
			nextJob = currentPhase.jobs.poll();
		}
		
		if (nextJob != null) {
			try {
				nextJob.run(this);
			} catch(Throwable e) {
				log.error("Rendering process job '" + nextJob + "' failed", e);
				processObserver.terminateProcess();
				observer.finishedWithError(e);
				return;
			}
			
			synchronized(phaseIter) {
				if (--currentPhase.pendingSize == 0) {
					if (phaseIter.hasNext()) {
						currentPhase = phaseIter.next();
					} else {
						log.info("Finished " + this + " in " + (System.currentTimeMillis() - startTime) + "ms");
						processObserver.terminateProcess();
						observer.finished();
					}
				}
			}
		}
	}

	@Override
	public URI getResourcePrefix() {
		return context.getResourcePrefix();
	}

	@Override
	public String toString() {
		return "RenderingProcess [" + context.getBundle().getName() + ']';
	}

	@Override
	public InputStream createInputStream(URI publicUri) throws ResourceNotFoundException {
		return context.createInputStream(publicUri);
	}

	@Override
	public IOutputTarget createOutputTarget(String publicPath) {
		return context.createOutputTarget(publicPath);
	}
}
*/