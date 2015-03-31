package de.algorythm.cms.common.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.IRendererFactory;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.resources.IBundleLoader;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.impl.ClasspathInputSourceResolver;
import de.algorythm.cms.common.resources.impl.FileInputSourceResolver;
import de.algorythm.cms.common.scheduling.IProcessScheduler;

public class CmsCommonFacade implements ICmsCommonFacade {

	private final IBundleExpander bundleExpander;
	private final IBundleLoader bundleLoader;
	private final IProcessScheduler scheduler;
	private final IRendererFactory rendererFactory;

	@Inject
	public CmsCommonFacade(final IBundleLoader bundleLoader,
			final IBundleExpander bundleExpander,
			final IProcessScheduler scheduler,
			final IRendererFactory rendererFactory) throws IOException {
		this.bundleLoader = bundleLoader;
		this.bundleExpander = bundleExpander;
		this.scheduler = scheduler;
		this.rendererFactory = rendererFactory;
	}

	@Override
	public IInputResolver createInputResolver(List<Path> rootDirectories) {
		return new FileInputSourceResolver(rootDirectories, new ClasspathInputSourceResolver());
	}

	@Override
	public IBundle loadBundle(URI bundleUri, IInputResolver resolver) {
		final IBundle bundle;
		
		try {
			bundle = bundleLoader.loadBundle(bundleUri, resolver);
		} catch (ResourceNotFoundException | IOException | JAXBException e) {
			throw new RuntimeException("Cannot load bundle '" + bundleUri + "' - " + e.getMessage(), e);
		}
		
		try {
			return bundleExpander.expandedBundle(bundle, resolver);
		} catch (ResourceNotFoundException | IOException | JAXBException e) {
			throw new RuntimeException("Cannot expand bundle '" + bundleUri + "' - " + e.getMessage(), e);
		}
	}

	@Override
	public IRenderer createRenderer(IBundle bundle, IInputResolver resolver) {
		return rendererFactory.createRenderer(bundle, resolver);
	}

	@Override
	public void shutdown() {
		scheduler.shutdown();
	}
}
