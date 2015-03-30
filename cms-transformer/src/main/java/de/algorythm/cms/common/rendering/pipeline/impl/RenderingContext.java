package de.algorythm.cms.common.rendering.pipeline.impl;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;

import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.FileOutputTargetFactory;
import de.algorythm.cms.common.resources.impl.ResourceResolver;

public class RenderingContext implements IRenderingContext {

	private final IBundle bundle;
	private final URI resourcePrefix;
	private final ISourcePathResolver sourceResolver;
	private final IOutputTargetFactory tmpOutputStreamFactory;
	private Collection<IPageConfig> renderPages;

	public RenderingContext(final IBundle bundle, final Path tmpDirectory, final URI resourcePrefix) {
		this.bundle = bundle;
		this.resourcePrefix = resourcePrefix;
		this.sourceResolver = new ResourceResolver(bundle, tmpDirectory);
		this.tmpOutputStreamFactory = new FileOutputTargetFactory(tmpDirectory);
	}

	public Collection<IPageConfig> getRenderPages() {
		return renderPages;
	}

	public void setRenderPages(Collection<IPageConfig> renderPages) {
		this.renderPages = renderPages;
	}

	@Override
	public IBundle getBundle() {
		return bundle;
	}

	@Override
	public URI getResourcePrefix() {
		return resourcePrefix;
	}

	@Override
	public IOutputTarget createOutputTarget(String publicPath) {
		return tmpOutputStreamFactory.createOutputTarget(publicPath);
	}

	@Override
	public Path resolveSource(URI uri) throws ResourceNotFoundException {
		return sourceResolver.resolveSource(uri);
	}
}