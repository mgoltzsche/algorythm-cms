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
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.bundle.IOutputConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig.RenderingPhase;
import de.algorythm.cms.common.model.entity.impl.XmlTemplates;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.rendering.pipeline.job.JavascriptCompressor;
import de.algorythm.cms.common.rendering.pipeline.job.PageIndexer;
import de.algorythm.cms.common.rendering.pipeline.job.PageTransformer;
import de.algorythm.cms.common.rendering.pipeline.job.ScssCompiler;
import de.algorythm.cms.common.rendering.pipeline.job.SupportedLocalesXmlGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.SvgSpriteGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.TemplateCompiler;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.scheduling.IExecutor;
import de.algorythm.cms.common.scheduling.IFuture;
import de.algorythm.cms.common.scheduling.IProcessScheduler;
import de.algorythm.cms.common.scheduling.impl.Future;

public class Renderer implements IRenderer {

	private final IBundle bundle;
	private final Path tmpDirectory;
	private final IRenderingContext ctx;
	
	private final SupportedLocalesXmlGenerator localesXmlGenerator;
	private final PageIndexer indexer;
	private final TemplateCompiler templateCompiler;
	private final PageTransformer transformer;
	private final JavascriptCompressor jsCompressor;
	private final ScssCompiler scssCompiler;
	private final SvgSpriteGenerator svgSpriteGenerator;

	public Renderer(final IBundle expandedBundle,
			final Path tmpDirectory,
			final SupportedLocalesXmlGenerator localesXmlGenerator,
			final PageIndexer indexer,
			final TemplateCompiler templateCompiler,
			final PageTransformer transformer,
			final JavascriptCompressor jsCompressor,
			final ScssCompiler scssCompiler,
			final SvgSpriteGenerator svgSpriteGenerator) {
		this.bundle = expandedBundle;
		this.tmpDirectory = tmpDirectory;
		final URI resourcePrefix = URI.create("/r/" + new Date().getTime() + '/');
		this.ctx = new RenderingContext(expandedBundle, tmpDirectory, resourcePrefix);
		
		this.localesXmlGenerator = localesXmlGenerator;
		this.indexer = indexer;
		this.templateCompiler = templateCompiler;
		this.transformer = transformer;
		this.jsCompressor = jsCompressor;
		this.scssCompiler = scssCompiler;
		this.svgSpriteGenerator = svgSpriteGenerator;
	}

	public void renderAll(IExecutor executor, IOutputTargetFactory targetFactory) throws Exception {
		IOutputConfig output = bundle.getOutput(format);
		final Templates templates = templateCompiler.compileTemplates(output, ctx);
		final Set<URI> styles = mergeUris(output.getModule().getStyles(), output.getTheme().getStyles());
		final Set<URI> scripts = mergeUris(output.getModule().getScripts(), output.getTheme().getScripts());
		
		localesXmlGenerator.generateSupportedLocalesXml(ctx.getBundle(), true, targetFactory);
		indexer.indexPages(ctx, executor);
		transformer.transformPages(ctx, templates, executor, targetFactory);
		jsCompressor.compressJs(ctx, scripts, targetFactory);
		scssCompiler.compileScss(ctx, styles, targetFactory);
		//svgSpriteGenerator.generateSvgSprite(ctx, sources, flagDirectoryUri, true, targetFactory);
	}

	public void renderPage(IExecutor executor) {
		transformer.transformPage(ctx, sourceUri, path, relativeRootPath, compiledTemplates, locale, resourceBasePath, targetFactory)
	}
	
	private Set<URI> mergeUris(Set<URI> uris1, Set<URI> uris2) {
		final Set<URI> merged = new HashSet<URI>();
		
		merged.addAll(uris1);
		merged.addAll(uris2);
		
		return merged;
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
		final IRenderingContext ctx = new RenderingContext(bundle, tmpDirectory, resourceOutputPath);
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
		
		scheduler.execute(new RenderingProcess(ctx, processJobs, future));
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