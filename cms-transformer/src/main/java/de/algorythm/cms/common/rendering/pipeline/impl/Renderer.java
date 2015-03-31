package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;

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
import de.algorythm.cms.common.rendering.pipeline.job.JavascriptCompressor;
import de.algorythm.cms.common.rendering.pipeline.job.PageIndexer;
import de.algorythm.cms.common.rendering.pipeline.job.PageTransformer;
import de.algorythm.cms.common.rendering.pipeline.job.ScssCompiler;
import de.algorythm.cms.common.rendering.pipeline.job.SupportedLocalesXmlGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.SvgSpriteGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.TemplateCompiler;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class Renderer implements IRenderer {

	private final IBundle bundle;
	private final IRenderingContext ctx;
	
	private final SupportedLocalesXmlGenerator localesXmlGenerator;
	private final PageIndexer indexer;
	private final TemplateCompiler templateCompiler;
	private final PageTransformer transformer;
	private final JavascriptCompressor jsCompressor;
	private final ScssCompiler scssCompiler;
	private final SvgSpriteGenerator svgSpriteGenerator;

	public Renderer(final IBundle expandedBundle,
			final IInputResolver resolver,
			final SupportedLocalesXmlGenerator localesXmlGenerator,
			final PageIndexer indexer,
			final TemplateCompiler templateCompiler,
			final PageTransformer transformer,
			final JavascriptCompressor jsCompressor,
			final ScssCompiler scssCompiler,
			final SvgSpriteGenerator svgSpriteGenerator) {
		final FileSystem tmpFs = Jimfs.newFileSystem(Configuration.unix());
		final Path tmpDirectory = tmpFs.getPath("/expanded");
		final Path metaDirectory = tmpFs.getPath("/meta");
		final URI resourcePrefix = URI.create("/r/" + new Date().getTime() + '/');
		
		this.ctx = new RenderingContext(expandedBundle.getTitle(), tmpDirectory, metaDirectory, resourcePrefix, resolver);
		this.bundle = expandedBundle;
		this.localesXmlGenerator = localesXmlGenerator;
		this.indexer = indexer;
		this.templateCompiler = templateCompiler;
		this.transformer = transformer;
		this.jsCompressor = jsCompressor;
		this.scssCompiler = scssCompiler;
		this.svgSpriteGenerator = svgSpriteGenerator;
	}

	@Override
	public void renderAll(Format format, IOutputTargetFactory outFactory) throws Exception {
		final IOutputConfig output = bundle.getOutputMapping().get(format);
		
		if (output == null)
			throw new IllegalArgumentException("Bundle '" + bundle.getUri() + "' does not support output: " + format);
		
		final Map<String, String> params = Collections.emptyMap(); // TODO: Make configurable
		final Set<Locale> locales = bundle.getSupportedLocales();
		final IPage startPage = bundle.getStartPage();
		final Templates templates = templateCompiler.compileTemplates(output, ctx);
		final Set<URI> styles = mergeUris(output.getModule().getStyles(), output.getTheme().getStyles());
		final Set<URI> scripts = mergeUris(output.getModule().getScripts(), output.getTheme().getScripts());
		
		localesXmlGenerator.generateSupportedLocalesXml(locales, ctx.getTmpResources());
		indexPages(startPage, locales);
		renderPages(startPage, locales, params, templates, outFactory);
		jsCompressor.compressJs(ctx, scripts, outFactory);
		scssCompiler.compileScss(ctx, styles, outFactory);
		//svgSpriteGenerator.generateSvgSprite(ctx, sources, flagDirectoryUri, true, targetFactory);
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

	public void renderPage(IPage page, Locale locale, String path, Map<String, String> params, Templates templates, IOutputTargetFactory outFactory) throws IOException, TransformerException, SAXException, ParserConfigurationException, JAXBException, ResourceNotFoundException {
		transformer.transformPage(page.getSource(), locale, path, params, templates, ctx, "index.html", outFactory);
	}

	private Set<URI> mergeUris(Set<URI> uris1, Set<URI> uris2) {
		final Set<URI> merged = new HashSet<URI>();
		
		merged.addAll(uris1);
		merged.addAll(uris2);
		
		return merged;
	}
}