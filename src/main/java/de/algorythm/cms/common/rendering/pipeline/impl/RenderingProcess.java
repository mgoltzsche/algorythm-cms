package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Injector;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.IResourceResolver;
import de.algorythm.cms.common.scheduling.IProcess;
import de.algorythm.cms.common.scheduling.IProcessObserver;

public class RenderingProcess implements IProcess, IRenderingContext {

	static private class PipelinePhase {
		
		public final LinkedList<IRenderingJob> jobs;
		public int pendingSize;
		
		public PipelinePhase(final LinkedList<IRenderingJob> jobs) {
			this.jobs = jobs;
			this.pendingSize = jobs.size();
		}
	}
	
	private final Injector injector;
	private final IBundleRenderingContext context;
	private final Iterator<PipelinePhase> phaseIter;
	private PipelinePhase currentPhase;

	public RenderingProcess(final IBundleRenderingContext context, final List<Collection<IRenderingJob>> jobPhases, final Injector injector) {
		this.context = context;
		final LinkedList<PipelinePhase> phases = new LinkedList<PipelinePhase>();
		
		for (Collection<IRenderingJob> jobs : jobPhases) {
			phases.add(new PipelinePhase(new LinkedList<IRenderingJob>(jobs)));
			
			for (IRenderingJob job : jobs)
				injector.injectMembers(job);
		}
		
		phaseIter = phases.iterator();
		currentPhase = phaseIter.next();
		this.injector = injector;
	}

	@Override
	public void runProcess(final IProcessObserver processObserver) {
		final IRenderingJob nextJob;
		
		synchronized(phaseIter) {
			nextJob = currentPhase.jobs.poll();
		}
		
		if (nextJob != null) {
			nextJob.run(this);
			
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
	public void execute(final IRenderingJob job) {
		injector.injectMembers(job);
		
		synchronized(phaseIter) {
			currentPhase.jobs.add(job);
			currentPhase.pendingSize++;
		}
	}

	@Override
	public IBundle getBundle() {
		return context.getBundle();
	}

	@Override
	public IResourceResolver getResourceResolver() {
		return context.getResourceResolver();
	}

	@Override
	public File getTempDirectory() {
		return context.getTempDirectory();
	}

	@Override
	public File getOutputDirectory() {
		return context.getOutputDirectory();
	}

	@Override
	public String getProperty(String name) {
		return context.getProperty(name);
	}

	@Override
	public void setProperty(String name, String value) {
		context.setProperty(name, value);
	}
}
