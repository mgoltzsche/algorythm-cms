package de.algorythm.cms.common.impl;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import de.algorythm.cms.common.IRendererFactory;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IMetadataExtractorProvider;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.IXmlSourceResolverProvider;
import de.algorythm.cms.common.rendering.pipeline.impl.Renderer;
import de.algorythm.cms.common.rendering.pipeline.job.JavascriptCompressor;
import de.algorythm.cms.common.rendering.pipeline.job.PageIndexer;
import de.algorythm.cms.common.rendering.pipeline.job.PageTransformer;
import de.algorythm.cms.common.rendering.pipeline.job.ScssCompiler;
import de.algorythm.cms.common.rendering.pipeline.job.SupportedLocalesXmlGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.SvgSpriteGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.TemplateCompiler;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;

@Singleton
public class RendererFactory implements IRendererFactory {

	private final IXmlSourceResolver xmlSourceResolver;
	private final IMetadataExtractor metadataExtractor;
	private final JAXBContext jaxbContext;
	private final IBundleExpander expander;
	// transformation jobs
	private final TemplateCompiler templateCompiler;
	private final SupportedLocalesXmlGenerator localesXmlGenerator;
	private final PageIndexer indexer;
	private final PageTransformer transformer;
	private final JavascriptCompressor jsCompressor;
	private final ScssCompiler scssCompiler;
	private final SvgSpriteGenerator svgSpriteGenerator;

	@Inject
	public RendererFactory(
			final IXmlSourceResolverProvider xmlSourceResolverProvider,
			final IMetadataExtractorProvider metadataExtractorProvider,
			final JAXBContext jaxbContext, final IBundleExpander expander,
			final TemplateCompiler templateCompiler,
			final SupportedLocalesXmlGenerator localesXmlGenerator,
			final PageIndexer indexer,
			final PageTransformer transformer,
			final JavascriptCompressor jsCompressor,
			final ScssCompiler scssCompiler,
			final SvgSpriteGenerator svgSpriteGenerator) {
		this.xmlSourceResolver = xmlSourceResolverProvider.getXmlSourceResolver();
		this.metadataExtractor = metadataExtractorProvider.getMetadataExtractor();
		this.jaxbContext = jaxbContext;
		this.expander = expander;
		this.templateCompiler = templateCompiler;
		this.localesXmlGenerator = localesXmlGenerator;
		this.indexer = indexer;
		this.transformer = transformer;
		this.jsCompressor = jsCompressor;
		this.scssCompiler = scssCompiler;
		this.svgSpriteGenerator = svgSpriteGenerator;
	}

	@Override
	public IRenderer createRenderer(IBundle bundle) {
		final FileSystem tmpFs = Jimfs.newFileSystem(Configuration.unix());
		final Path tmpDirectory = tmpFs.getPath("/");
		final IBundle expandedBundle = expander.expandBundle(bundle, tmpDirectory);
		
		return new Renderer(xmlSourceResolver,
				metadataExtractor, jaxbContext,
				expandedBundle, tmpDirectory,
				localesXmlGenerator, indexer, templateCompiler, transformer,
				jsCompressor, scssCompiler, svgSpriteGenerator);
	}
}
