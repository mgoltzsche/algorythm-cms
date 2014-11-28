package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.inject.Injector;

import de.algorythm.cms.common.model.entity.IOutputConfiguration;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfiguration;
import de.algorythm.cms.common.model.entity.IRenderingJobConfiguration.PipelinePhase;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.IResourceResolver;
import de.algorythm.cms.common.scheduling.IProcessScheduler;

public class Renderer implements IRenderer {

	@Inject
	private IProcessScheduler scheduler;
	@Inject
	private Injector injector;
	
	@Override
	public void render(final IResourceResolver resourceResolver, final File tmpDirectory, final File outputDirectory, final Iterable<IOutputConfiguration> outputCfgs) {
		final IBundleRenderingContext context = new RenderingContext(resourceResolver, tmpDirectory, outputDirectory);
		final Map<PipelinePhase, Set<IRenderingJob>> pipelineMap = new HashMap<PipelinePhase, Set<IRenderingJob>>();
		
		for (IOutputConfiguration outputCfg : outputCfgs) {
			for (IRenderingJobConfiguration jobCfg : outputCfg.getJobs()) {
				if (!jobCfg.isEnabled())
					continue;
				
				Set<IRenderingJob> phaseJobs = pipelineMap.get(jobCfg.getPhase());
				
				if (phaseJobs == null) {
					phaseJobs = new LinkedHashSet<IRenderingJob>();
					pipelineMap.put(jobCfg.getPhase(), phaseJobs);
				}
				
				phaseJobs.add(createJob(jobCfg));
			}
		}
		
		final LinkedList<Collection<IRenderingJob>> processJobs = new LinkedList<Collection<IRenderingJob>>();
		
		for (PipelinePhase phase : PipelinePhase.values()) {
			final Set<IRenderingJob> jobs = pipelineMap.get(phase);
			
			if (jobs != null)
				processJobs.add(jobs);
		}
		
		scheduler.execute(new RenderingProcess(context, processJobs, injector));
	}
	
	private IRenderingJob createJob(IRenderingJobConfiguration jobCfg) {
		final Class<?> jobType = jobCfg.getJobType();
		final IRenderingJob job;
		
		if (!IRenderingJob.class.isAssignableFrom(jobType))
			throw new IllegalArgumentException("Job type " + jobType + " does not implement " + IRenderingJob.class);
		
		try {
			job = (IRenderingJob) jobType.newInstance();
		} catch(Exception e) {
			throw new RuntimeException("Cannot create job of type " + jobType);
		}
		
		final Set<String> finalParams = new HashSet<String>();
		
		for (IParam param : jobCfg.getParams())
			addProperty(job, param, finalParams);
		
		return job;
	}
	
	public void addProperty(final IRenderingJob job, final IParam param, final Set<String> finalParams) {
		final Class<?> jobType = job.getClass();
		
		try {
			final Field field = jobType.getDeclaredField(param.getId());
			final Class<?> fieldType = field.getType();
			
			field.setAccessible(true);
			
			if (Collection.class.isAssignableFrom(field.getType())) {
				final Collection<String> listProperty = (Collection<String>) field.get(job);
				
				listProperty.add(param.getValue());
			} else if (finalParams.add(param.getId())) {
				if (fieldType == String.class) {
					field.set(job, param.getValue());
				} else if (boolean.class == fieldType || Boolean.class == fieldType) {
					field.set(job, Boolean.parseBoolean(param.getValue()));
				} else if (int.class == fieldType || Integer.class == fieldType) {
					field.set(job, Integer.parseInt(param.getValue()));
				} else {
					throw new UnsupportedOperationException("Unsupported parameter " + jobType.getName() + '.' + param.getId());
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Cannot set parameter " + jobType.getName() + '.' + param.getId(), e);
		}
	}
}