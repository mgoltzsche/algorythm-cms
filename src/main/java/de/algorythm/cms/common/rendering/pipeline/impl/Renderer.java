package de.algorythm.cms.common.rendering.pipeline.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.inject.Inject;

import com.google.inject.Injector;

import de.algorythm.cms.common.model.entity.IOutputConfiguration;
import de.algorythm.cms.common.model.entity.IRenderingPipelineTaskConfiguration;
import de.algorythm.cms.common.model.entity.IRenderingPipelineTaskConfiguration.PipelinePhase;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.scheduling.IProcessScheduler;

public class Renderer {

	@Inject
	private IProcessScheduler scheduler;
	@Inject
	private Injector injector;
	
	public IRenderingPipeline createRenderingPipeline(final IOutputConfiguration pipelineCfg) {
		final Map<PipelinePhase, LinkedList<IRenderingJob>> pipelineCfgMap = new HashMap<PipelinePhase, LinkedList<IRenderingJob>>();
		
		for (IRenderingPipelineTaskConfiguration jobCfg : pipelineCfg.getTasks()) {
			LinkedList<IRenderingJob> phaseJobs = pipelineCfgMap.get(jobCfg.getPhase());
			
			if (phaseJobs == null) {
				phaseJobs = new LinkedList<IRenderingJob>();
				pipelineCfgMap.put(jobCfg.getPhase(), phaseJobs);
			}
			
			final Class<?> jobType = jobCfg.getTaskType();
			final IRenderingJob job;
			
			if (!IRenderingJob.class.isAssignableFrom(jobType))
				throw new IllegalArgumentException("Job type " + jobType + " does not implement " + IRenderingJob.class);
			
			try {
				job = (IRenderingJob) jobType.newInstance();
			} catch(Exception e) {
				throw new RuntimeException("Cannot create job of type " + jobType);
			}
			
			phaseJobs.add(job);
		}
		
		final LinkedList<LinkedList<IRenderingJob>> processJobs = new LinkedList<LinkedList<IRenderingJob>>();
		
		for (PipelinePhase phase : PipelinePhase.values()) {
			final LinkedList<IRenderingJob> jobs = pipelineCfgMap.get(phase);
			
			if (jobs != null)
				processJobs.add(jobs);
		}
		
		// TODO: Create process
	}
}