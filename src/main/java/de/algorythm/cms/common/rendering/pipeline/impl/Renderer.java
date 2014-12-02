package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.inject.Injector;

import de.algorythm.cms.common.model.entity.IOutputConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig.RenderingPhase;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IResourceResolver;
import de.algorythm.cms.common.resources.impl.OutputUriResolver;
import de.algorythm.cms.common.scheduling.IFuture;
import de.algorythm.cms.common.scheduling.IProcessScheduler;
import de.algorythm.cms.common.scheduling.impl.Future;

public class Renderer implements IRenderer {

	@Inject
	private IProcessScheduler scheduler;
	@Inject
	private Injector injector;
	
	@Override
	public IFuture render(final IResourceResolver uriResolver, final File tmpDirectory, final File outputDirectory, final Iterable<IOutputConfig> outputCfgs) {
		final String resourceOutputPrefix = "/r/" + new Date().getTime();
		final IOutputUriResolver outputUriResolver = new OutputUriResolver(outputDirectory.toURI(), resourceOutputPrefix);
		final IBundleRenderingContext context = new RenderingContext(uriResolver, outputUriResolver, resourceOutputPrefix, tmpDirectory, outputDirectory);
		final Map<RenderingPhase, Set<IRenderingJob>> phaseMap = new HashMap<RenderingPhase, Set<IRenderingJob>>();
		
		for (IOutputConfig outputCfg : outputCfgs) {
			for (IRenderingJobConfig jobCfg : outputCfg.getJobs()) {
				if (!jobCfg.isEnabled())
					continue;
				
				Set<IRenderingJob> phaseJobs = phaseMap.get(jobCfg.getPhase());
				
				if (phaseJobs == null) {
					phaseJobs = new LinkedHashSet<IRenderingJob>();
					phaseMap.put(jobCfg.getPhase(), phaseJobs);
				}
				
				phaseJobs.add(initializeJob(jobCfg, uriResolver));
			}
		}
		
		final LinkedList<Collection<IRenderingJob>> processJobs = new LinkedList<Collection<IRenderingJob>>();
		
		for (RenderingPhase phase : RenderingPhase.values()) {
			final Set<IRenderingJob> jobs = phaseMap.get(phase);
			
			if (jobs != null)
				processJobs.add(jobs);
		}
		
		final Future future = new Future();
		
		scheduler.execute(new RenderingProcess(context, processJobs, injector, future));
		
		return future;
	}
	
	private IRenderingJob initializeJob(final IRenderingJobConfig jobCfg, final IResourceResolver uriResolver) {
		final Class<?> jobType = jobCfg.getJobType();
		final IRenderingJob job;
		
		if (!IRenderingJob.class.isAssignableFrom(jobType))
			throw new IllegalArgumentException("Job type " + jobType + " does not implement " + IRenderingJob.class);
		
		try {
			job = (IRenderingJob) jobType.newInstance();
		} catch(Exception e) {
			throw new RuntimeException("Cannot create job of type " + jobType + ". " + e.getMessage(), e);
		}
		
		final Set<String> finalParams = new HashSet<String>();
		
		for (IParam param : jobCfg.getParams())
			addParam(job, param, finalParams, uriResolver);
		
		return job;
	}
	
	public void addParam(final IRenderingJob job, final IParam param, final Set<String> finalParams, final IResourceResolver uriResolver) {
		final Class<?> jobType = job.getClass();
		
		try {
			final Field field = jobType.getDeclaredField(param.getId());
			final Class<?> fieldType = field.getType();
			
			field.setAccessible(true);
			
			if (Collection.class.isAssignableFrom(field.getType())) {
				final Collection<?> list = (Collection<?>) field.get(job);
				
				if (list == null)
					throw new IllegalStateException("Collection field is null");
				
				addListValue(list, param, field, uriResolver);
			} else if (finalParams.add(param.getId())) {
				field.set(job, convertParamValue(param, fieldType, uriResolver));
			}
		} catch(Exception e) {
			throw new RuntimeException("Cannot set parameter " + jobType.getName() + '.' + param.getId() + " due to " + e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}
	
	private <V> void addListValue(final Collection<V> list, final IParam param, final Field field, final IResourceResolver uriResolver) throws FileNotFoundException {
		final Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
		
		if (typeArguments == null || typeArguments.length == 0)
			throw new IllegalStateException("Collection field " + field.getDeclaringClass() + '.' + param.getId() + " has no type argument");
		
		@SuppressWarnings("unchecked")
		final Class<V> itemType = (Class<V>) typeArguments[0];
		@SuppressWarnings("unchecked")
		final V value = (V) convertParamValue(param, itemType, uriResolver);
		
		list.add(value);
	}
	
	private Object convertParamValue(final IParam param, final Class<?> fieldType, final IResourceResolver uriResolver) throws FileNotFoundException {
		if (fieldType == String.class) {
			return param.getValue();
		} else if (Boolean.class == fieldType || boolean.class == fieldType) {
			return Boolean.parseBoolean(param.getValue());
		} else if (Integer.class == fieldType || int.class == fieldType) {
			return Integer.parseInt(param.getValue());
		} else if (File.class == fieldType) {
			return new File(uriResolver.toSystemUri(URI.create(param.getValue()), uriResolver.getMergedBundle().getLocation().resolve("bundle.xml")));
		} else if (URI.class == fieldType) {
			return URI.create(param.getValue()).normalize();
		} else {
			throw new UnsupportedOperationException("Unsupported field type " + fieldType.getName());
		}
	}
}