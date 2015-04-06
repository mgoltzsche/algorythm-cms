package de.algorythm.cms.common.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.algorythm.cms.common.IRendererFactory;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.impl.Renderer;
import de.algorythm.cms.common.rendering.pipeline.job.JavaScriptCompressor;
import de.algorythm.cms.common.rendering.pipeline.job.PageIndexer;
import de.algorythm.cms.common.rendering.pipeline.job.PageTransformer;
import de.algorythm.cms.common.rendering.pipeline.job.ScssCompiler;
import de.algorythm.cms.common.rendering.pipeline.job.SupportedLocalesXmlGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.SvgSpriteGenerator;
import de.algorythm.cms.common.rendering.pipeline.job.TemplateCompiler;
import de.algorythm.cms.common.resources.IInputResolver;

@Singleton
public class RendererFactory implements IRendererFactory {

	private final TemplateCompiler templateCompiler;
	private final SupportedLocalesXmlGenerator localesXmlGenerator;
	private final PageIndexer indexer;
	private final PageTransformer transformer;
	private final JavaScriptCompressor jsCompressor;
	private final ScssCompiler scssCompiler;
	private final SvgSpriteGenerator svgSpriteGenerator;

	@Inject
	public RendererFactory(final TemplateCompiler templateCompiler,
			final SupportedLocalesXmlGenerator localesXmlGenerator,
			final PageIndexer indexer,
			final PageTransformer transformer,
			final JavaScriptCompressor jsCompressor,
			final ScssCompiler scssCompiler,
			final SvgSpriteGenerator svgSpriteGenerator) {
		this.templateCompiler = templateCompiler;
		this.localesXmlGenerator = localesXmlGenerator;
		this.indexer = indexer;
		this.transformer = transformer;
		this.jsCompressor = jsCompressor;
		this.scssCompiler = scssCompiler;
		this.svgSpriteGenerator = svgSpriteGenerator;
	}

	@Override
	public IRenderer createRenderer(IBundle bundle, IInputResolver resolver) {
		return new Renderer(bundle, resolver,
				localesXmlGenerator, indexer, templateCompiler, transformer,
				jsCompressor, scssCompiler, svgSpriteGenerator);
	}
}
