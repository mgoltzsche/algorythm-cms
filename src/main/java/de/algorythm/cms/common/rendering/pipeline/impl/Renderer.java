package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.Injector;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IOutputConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig.RenderingPhase;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.scheduling.IFuture;
import de.algorythm.cms.common.scheduling.IProcessScheduler;
import de.algorythm.cms.common.scheduling.impl.Future;

@Singleton
public class Renderer implements IRenderer {

	private final IProcessScheduler scheduler;
	private final Injector injector;

	@Inject
	public Renderer(final IProcessScheduler scheduler, final Injector injector) {
		this.scheduler = scheduler;
		this.injector = injector;
	}

	@Override
	public IFuture<Void> render(final IBundle bundle, final Path tmpDirectory, final Path outputDirectory) {
		final TimeMeter meter = TimeMeter.meter(bundle.getName() + " process initialization");
		final URI resourceOutputPath = URI.create("/r/" + new Date().getTime() + '/');
		final IBundleRenderingContext ctx = new RenderingContext(bundle, tmpDirectory, outputDirectory, resourceOutputPath);
		final Map<RenderingPhase, Set<IRenderingJob>> phaseMap = new HashMap<RenderingPhase, Set<IRenderingJob>>();
		final LinkedList<Collection<IRenderingJob>> processJobs = new LinkedList<Collection<IRenderingJob>>();
		final Future<Void> future = new Future<Void>();
		
		for (IOutputConfig outputCfg : bundle.getOutput()) {
			for (IRenderingJobConfig jobCfg : outputCfg.getJobs()) {
				if (!jobCfg.isEnabled())
					continue;
				
				Set<IRenderingJob> phaseJobs = phaseMap.get(jobCfg.getPhase());
				
				if (phaseJobs == null) {
					phaseJobs = new LinkedHashSet<IRenderingJob>();
					phaseMap.put(jobCfg.getPhase(), phaseJobs);
				}
				
				phaseJobs.add(initializeJob(jobCfg, ctx.getResourceResolver()));
			}
		}
		
		for (RenderingPhase phase : RenderingPhase.values()) {
			final Set<IRenderingJob> jobs = phaseMap.get(phase);
			
			if (jobs != null)
				processJobs.add(jobs);
		}
		
		scheduler.execute(new RenderingProcess(ctx, processJobs, injector, future));
		meter.finish();
	
		return future;
	}

	private IRenderingJob initializeJob(final IRenderingJobConfig jobCfg, final ISourceUriResolver sourceUriResolver) {
		final Class<?> jobType = jobCfg.getJobType();
		final IRenderingJob job;
		
		if (jobType == null)
			throw new IllegalArgumentException("Missing job type");
		
		if (!IRenderingJob.class.isAssignableFrom(jobType))
			throw new IllegalArgumentException("Job type " + jobType + " does not implement " + IRenderingJob.class);
		
		try {
			job = (IRenderingJob) jobType.newInstance();
		} catch(Exception e) {
			throw new RuntimeException("Cannot create job of type " + jobType + ". " + e.getMessage(), e);
		}
		
		final Set<String> finalParams = new HashSet<String>();
		
		for (IParam param : jobCfg.getParams())
			addParam(job, param, finalParams, sourceUriResolver);
		
		return job;
	}

	public void addParam(final IRenderingJob job, final IParam param, final Set<String> finalParams, final ISourceUriResolver sourceUriResolver) {
		final Class<?> jobType = job.getClass();
		
		try {
			final Field field = jobType.getDeclaredField(param.getId());
			final Class<?> fieldType = field.getType();
			
			field.setAccessible(true);
			
			if (Collection.class.isAssignableFrom(field.getType())) {
				final Collection<?> list = (Collection<?>) field.get(job);
				
				if (list == null)
					throw new IllegalStateException("Collection field is null");
				
				addListValue(list, param, field, sourceUriResolver);
			} else if (finalParams.add(param.getId())) {
				field.set(job, convertParamValue(param, fieldType, sourceUriResolver));
			}
		} catch(Exception e) {
			throw new RuntimeException("Cannot set parameter " + jobType.getName() + '.' + param.getId() + " due to " + e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	private <V> void addListValue(final Collection<V> list, final IParam param, final Field field, final ISourceUriResolver sourceUriResolver) throws FileNotFoundException {
		final Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
		
		if (typeArguments == null || typeArguments.length == 0)
			throw new IllegalStateException("Collection field " + field.getDeclaringClass() + '.' + param.getId() + " has no type argument");
		
		@SuppressWarnings("unchecked")
		final Class<V> itemType = (Class<V>) typeArguments[0];
		@SuppressWarnings("unchecked")
		final V value = (V) convertParamValue(param, itemType, sourceUriResolver);
		
		list.add(value);
	}

	private Object convertParamValue(final IParam param, final Class<?> fieldType, final ISourceUriResolver sourceUriResolver) throws FileNotFoundException {
		if (fieldType == String.class) {
			return param.getValue();
		} else if (Boolean.class == fieldType || boolean.class == fieldType) {
			return Boolean.parseBoolean(param.getValue());
		} else if (Integer.class == fieldType || int.class == fieldType) {
			return Integer.parseInt(param.getValue());
		} else if (Path.class == fieldType) {
			return sourceUriResolver.resolve(URI.create(param.getValue()).normalize());
		} else if (URI.class == fieldType) {
			return URI.create(param.getValue()).normalize();
		} else {
			throw new UnsupportedOperationException("Unsupported field type " + fieldType.getName());
		}
	}
}