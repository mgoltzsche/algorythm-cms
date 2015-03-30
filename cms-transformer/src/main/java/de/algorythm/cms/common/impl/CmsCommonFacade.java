package de.algorythm.cms.common.impl;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import de.algorythm.cms.common.ICmsCommonFacade;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.resources.IBundleLoader;
import de.algorythm.cms.common.scheduling.IProcessScheduler;

public class CmsCommonFacade implements ICmsCommonFacade {

	private final IBundleLoader bundleLoader;
	private final IProcessScheduler scheduler;
	private final RendererFactory rendererFactory;
	
	@Inject
	public CmsCommonFacade(final IBundleLoader bundleLoader,
			final IProcessScheduler scheduler,
			final RendererFactory rendererFactory) throws IOException {
		this.bundleLoader = bundleLoader;
		this.scheduler = scheduler;
		this.rendererFactory = rendererFactory;
	}
	
	@Override
	public IBundle loadBundle(final Path bundleXml) {
		try {
			return bundleLoader.loadBundle(bundleXml);
		} catch (IOException | JAXBException e) {
			throw new RuntimeException("Cannot load bundle in '" + bundleXml + "'. " + e.getMessage(), e);
		}
	}

	@Override
	public IRenderer createRenderer(final IBundle bundle) {
		return rendererFactory.createRenderer(bundle);
	}
	
	@Override
	public void shutdown() {
		scheduler.shutdown();
	}
}
