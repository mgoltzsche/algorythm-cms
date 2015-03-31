package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

import de.algorythm.cms.common.model.entity.bundle.IPage;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IInputSource;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.FileOutputTargetFactory;

public class RenderingContext implements IRenderingContext {

	private final String name;
	private final URI resourcePrefix;
	private final IInputResolver sourceResolver;
	private final IWriteableResources tmp, meta;
	private Collection<IPage> renderPages;

	public RenderingContext(String name, Path tmpDirectory, Path metaDirectory, URI resourcePrefix, IInputResolver sourceResolver) {
		this.name = name;
		this.resourcePrefix = resourcePrefix;
		this.sourceResolver = sourceResolver;
		this.tmp = new FileOutputTargetFactory(tmpDirectory);
		this.meta = new FileOutputTargetFactory(metaDirectory);
	}

	@Override
	public String getName() {
		return name;
	}

	public Collection<IPage> getRenderPages() {
		return renderPages;
	}

	public void setRenderPages(Collection<IPage> renderPages) {
		this.renderPages = renderPages;
	}

	@Override
	public URI getResourcePrefix() {
		return resourcePrefix;
	}

	@Override
	public IWriteableResources getMetaResources() {
		return meta;
	}

	@Override
	public IWriteableResources getTmpResources() {
		return tmp;
	}

	@Override
	public InputStream createInputStream(URI publicUri) throws ResourceNotFoundException, IOException {
		return sourceResolver.createInputStream(publicUri);
	}

	@Override
	public IInputSource resolveResource(URI publicUri)
			throws ResourceNotFoundException, IOException {
		return sourceResolver.resolveResource(publicUri);
	}
}