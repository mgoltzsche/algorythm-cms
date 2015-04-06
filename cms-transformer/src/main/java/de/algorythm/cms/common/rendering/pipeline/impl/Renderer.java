package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.bundle.IOutputConfig;
import de.algorythm.cms.common.model.entity.bundle.IPage;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.job.JavaScriptCompressor;
import de.algorythm.cms.common.rendering.pipeline.job.PageIndexer;
import de.algorythm.cms.common.rendering.pipeline.job.PageTransformer;
import de.algorythm.cms.common.rendering.pipeline.job.ScssCompiler;
import de.algorythm.cms.common.rendering.pipeline.job.SupportedLocalesXmlGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.SvgSpriteGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.TemplateCompiler;
import de.algorythm.cms.common.rendering.url.IUrlConstruction;
import de.algorythm.cms.common.rendering.url.LocalizedPath;
import de.algorythm.cms.common.rendering.url.impl.SimpleUrlConstruction;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.FileInputSourceResolver;
import de.algorythm.cms.common.resources.impl.SingleOutputTargetFactory;

public class Renderer implements IRenderer {

	private final IBundle bundle;
	private final IRenderingContext ctx;
	
	private final SupportedLocalesXmlGenerator localesXmlGenerator;
	private final PageIndexer indexer;
	private final TemplateCompiler templateCompiler;
	private final PageTransformer transformer;
	private final JavaScriptCompressor jsCompressor;
	private final ScssCompiler scssCompiler;
	private final SvgSpriteGenerator svgSpriteGenerator;
	private final Map<String, String> params = Collections.emptyMap(); // TODO: Make configurable
	private final Map<String, IPage> pageMapping = new HashMap<>();
	private final Templates htmlTemplates;
	private final IUrlConstruction urlConstruction;

	public Renderer(final IBundle expandedBundle,
			IInputResolver resolver,
			final SupportedLocalesXmlGenerator localesXmlGenerator,
			final PageIndexer indexer,
			final TemplateCompiler templateCompiler,
			final PageTransformer transformer,
			final JavaScriptCompressor jsCompressor,
			final ScssCompiler scssCompiler,
			final SvgSpriteGenerator svgSpriteGenerator) {
		final FileSystem tmpFs = Jimfs.newFileSystem(Configuration.unix());
		final Path tmpDirectory = tmpFs.getPath("/expanded");
		final Path metaDirectory = tmpFs.getPath("/meta");
		final URI resourcePrefix = URI.create("/r/" + new Date().getTime() + '/');
		
		resolver = new FileInputSourceResolver(tmpDirectory, resolver);
		this.ctx = new RenderingContext(expandedBundle.getTitle(), tmpDirectory, metaDirectory, resourcePrefix, resolver);
		this.bundle = expandedBundle;
		this.localesXmlGenerator = localesXmlGenerator;
		this.indexer = indexer;
		this.templateCompiler = templateCompiler;
		this.transformer = transformer;
		this.jsCompressor = jsCompressor;
		this.scssCompiler = scssCompiler;
		this.svgSpriteGenerator = svgSpriteGenerator;
		
		//urlConstruction = bundle.getSupportedLocales().size() == 1 ? new SimpleUrlConstruction() : new LocalizedUrlConstruction();
		urlConstruction = new SimpleUrlConstruction();
		
		mapPages(bundle.getStartPage(), "", pageMapping);
		
		try {
			htmlTemplates = templateCompiler.compileTemplates(bundle.getOutputMapping().get(Format.HTML), ctx);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void renderAll(Format format, IOutputTargetFactory outFactory) throws Exception {
		final IOutputConfig sources = getResources(format);
		final Set<Locale> locales = bundle.getSupportedLocales();
		final IPage startPage = bundle.getStartPage();
		final Templates templates = templateCompiler.compileTemplates(sources, ctx);
		
		expand();
		renderPages(startPage, locales, params, templates, outFactory);
		renderStaticResources(format, outFactory);
	}

	@Override
	public void expand() throws Exception {
		final Set<Locale> locales = bundle.getSupportedLocales();
		final IPage startPage = bundle.getStartPage();
		final IWriteableResources tmp = ctx.getTmpResources();
		
		localesXmlGenerator.generateSupportedLocalesXml(locales, tmp);
		indexPages(startPage, locales);
	}
	
	@Override
	public void renderStaticResources(Format format, IOutputTargetFactory outFactory) throws Exception {
		final IOutputConfig sources = getResources(format);
		final Set<URI> styles = mergeUris(sources.getModule().getStyles(), sources.getTheme().getStyles());
		final Set<URI> scripts = mergeUris(sources.getModule().getScripts(), sources.getTheme().getScripts());
		final Set<URI> svgIcons = mergeUris(sources.getModule().getSvgIcons(), sources.getTheme().getSvgIcons());
		
		jsCompressor.compressJs(ctx, scripts, outFactory);
		scssCompiler.compileScss(ctx, styles, outFactory);
		svgSpriteGenerator.generateSvgSprite(ctx, svgIcons, bundle.getSupportedLocales(), outFactory);
	}

	@Override
	public byte[] renderArtifact(URI outputUri) throws Exception, ResourceNotFoundException {
		final String outputFile = outputUri.normalize().getPath();
		final LocalizedPath localizedPath = urlConstruction.fromUrl(outputFile, bundle);
		final Locale locale = localizedPath.getLocale();
		final String unlocalizedOutputFile = localizedPath.getPath();
		final String extension = FilenameUtils.getExtension(outputFile).toLowerCase();
		final int lastSlashPos = unlocalizedOutputFile.lastIndexOf('/');
		final String pagePath = lastSlashPos == -1
				? unlocalizedOutputFile
				: unlocalizedOutputFile.substring(0, lastSlashPos);
		
		final IPage page = pageMapping.get(pagePath);
		
		if (page == null)
			throw new ResourceNotFoundException("Unknown path: " + pagePath);
		
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final SingleOutputTargetFactory outFactory = new SingleOutputTargetFactory(stream, outputFile);
		
		renderPage(page, locale, pagePath, params, htmlTemplates, outFactory);
		
		if (!outFactory.isOutputWritten())
			throw new ResourceNotFoundException("Resource " + outputUri + " does not exist");
		
		return stream.toByteArray();
	}
	
	private void mapPages(IPage page, String path, Map<String, IPage> pageMapping) {
		pageMapping.put(path, page);
		
		for (IPage child : page.getPages())
			mapPages(child, path + '/' + child.getName(), pageMapping);
	}
	
	private void indexPages(IPage startPage, Set<Locale> locales) throws Exception {
		for (Locale locale : locales)
			indexer.indexPages(startPage, locale, ctx);
	}

	private void renderPages(IPage startPage, Set<Locale> locales, Map<String, String> params, Templates templates, IOutputTargetFactory outFactory) throws Exception {
		for (Locale locale : locales)
			renderPagesRecursive(startPage, locale, StringUtils.EMPTY, params, templates, outFactory);
	}
	
	private void renderPagesRecursive(IPage page, Locale locale, String path, Map<String, String> params, Templates templates, IOutputTargetFactory outFactory) throws Exception {
		renderPage(page, locale, path, params, templates, outFactory);
		
		for (IPage child : page.getPages()) {
			final String childPath = path + '/' + child.getName();
			
			renderPagesRecursive(child, locale, childPath, params, templates, outFactory);
		}
	}

	private void renderPage(IPage page, Locale locale, String path, Map<String, String> params, Templates templates, IOutputTargetFactory outFactory) throws IOException, TransformerException, SAXException, ParserConfigurationException, JAXBException, ResourceNotFoundException {
		transformer.transformPage(page.getSource(), locale, path, params, templates, ctx, "index.html", outFactory);
	}

	private Set<URI> mergeUris(Set<URI> uris1, Set<URI> uris2) {
		final Set<URI> merged = new LinkedHashSet<URI>();
		
		merged.addAll(uris1);
		merged.addAll(uris2);
		
		return merged;
	}
	
	private IOutputConfig getResources(Format format) {
		final IOutputConfig sources = bundle.getOutputMapping().get(format);
		
		if (sources == null)
			throw new IllegalArgumentException("Bundle '" + bundle.getUri() + "' does not support output: " + format);
		
		return sources;
	}
}