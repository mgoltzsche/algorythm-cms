package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;

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
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.scheduling.IFuture;
import de.algorythm.cms.common.scheduling.IProcessScheduler;
import de.algorythm.cms.common.scheduling.impl.Future;

public class Renderer implements IRenderer {

	private final IProcessScheduler scheduler;
	private final Injector injector;
	private final IXmlSourceResolver xmlSourceResolver;
	private final JAXBContext jaxbContext;
	private final IMetadataExtractor metadataExtractor;
	private final IBundle bundle;
	private final Path tmpDirectory;

	public Renderer(final IProcessScheduler scheduler, final Injector injector,
			final IXmlSourceResolver xmlSourceResolver,
			final JAXBContext jaxbContext,
			final IMetadataExtractor metadataExtractor,
			final IBundle expandedBundle,
			final Path tmpDirectory) {
		this.scheduler = scheduler;
		this.injector = injector;
		this.xmlSourceResolver = xmlSourceResolver;
		this.jaxbContext = jaxbContext;
		this.metadataExtractor = metadataExtractor;
		
		this.bundle = expandedBundle;
		this.tmpDirectory = tmpDirectory;
	}

	@Override
	public IFuture<Void> render(final Path outputDirectory) {
		try {
			if (Files.exists(outputDirectory))
				deleteDirectory(outputDirectory);
			
			Files.createDirectories(outputDirectory);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		final TimeMeter meter = TimeMeter.meter(bundle.getName() + " process initialization");
		final URI resourceOutputPath = URI.create("/r/" + new Date().getTime() + '/');
		final IBundleRenderingContext ctx = new RenderingContext(bundle, metadataExtractor, jaxbContext, xmlSourceResolver, tmpDirectory, outputDirectory, resourceOutputPath);
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
				
				phaseJobs.add(initializeJob(jobCfg, ctx));
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

	/*@Override
	public byte[] render(final String pagePath, final OutputFormat format) {
		
	}*/
	
	private IRenderingJob initializeJob(final IRenderingJobConfig jobCfg, final ISourcePathResolver sourcePathResolver) {
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
			addParam(job, param, finalParams, sourcePathResolver);
		
		return job;
	}

	public void addParam(final IRenderingJob job, final IParam param, final Set<String> finalParams, final ISourcePathResolver sourcePathResolver) {
		final Class<?> jobType = job.getClass();
		
		try {
			final Field field = jobType.getDeclaredField(param.getId());
			final Class<?> fieldType = field.getType();
			
			field.setAccessible(true);
			
			if (Collection.class.isAssignableFrom(field.getType())) {
				final Collection<?> list = (Collection<?>) field.get(job);
				
				if (list == null)
					throw new IllegalStateException("Collection field is null");
				
				addListValue(list, param, field, sourcePathResolver);
			} else if (finalParams.add(param.getId())) {
				field.set(job, convertParamValue(param, fieldType, sourcePathResolver));
			}
		} catch(Exception e) {
			throw new RuntimeException("Cannot set parameter " + jobType.getName() + '.' + param.getId() + " due to " + e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	private <V> void addListValue(final Collection<V> list, final IParam param, final Field field, final ISourcePathResolver sourcePathResolver) throws FileNotFoundException, ResourceNotFoundException {
		final Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
		
		if (typeArguments == null || typeArguments.length == 0)
			throw new IllegalStateException("Collection field " + field.getDeclaringClass() + '.' + param.getId() + " has no type argument");
		
		@SuppressWarnings("unchecked")
		final Class<V> itemType = (Class<V>) typeArguments[0];
		@SuppressWarnings("unchecked")
		final V value = (V) convertParamValue(param, itemType, sourcePathResolver);
		
		list.add(value);
	}

	private Object convertParamValue(final IParam param, final Class<?> fieldType, final ISourcePathResolver sourcePathResolver) throws FileNotFoundException, ResourceNotFoundException {
		if (fieldType == String.class) {
			return param.getValue();
		} else if (Boolean.class == fieldType || boolean.class == fieldType) {
			return Boolean.parseBoolean(param.getValue());
		} else if (Integer.class == fieldType || int.class == fieldType) {
			return Integer.parseInt(param.getValue());
		} else if (Path.class == fieldType) {
			return sourcePathResolver.resolveSource(URI.create(param.getValue()).normalize());
		} else if (URI.class == fieldType) {
			return URI.create(param.getValue()).normalize();
		} else {
			throw new UnsupportedOperationException("Unsupported field type " + fieldType.getName());
		}
	}
	
	private void deleteDirectory(final Path directory) throws IOException {
		Files.walkFileTree(directory, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				throw exc;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				Files.delete(dir);
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
}