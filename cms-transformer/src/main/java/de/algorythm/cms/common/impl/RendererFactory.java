package de.algorythm.cms.common.impl;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.inject.Injector;

import de.algorythm.cms.common.IRendererFactory;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.impl.Renderer;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.scheduling.IProcessScheduler;

@Singleton
public class RendererFactory implements IRendererFactory {

	private final IProcessScheduler scheduler;
	private final Injector injector;
	private final IXmlSourceResolver xmlSourceResolver;
	private final JAXBContext jaxbContext;
	private final IMetadataExtractor metadataExtractor;
	private final IBundleExpander expander;

	@Inject
	public RendererFactory(final IProcessScheduler scheduler, final Injector injector, final IXmlSourceResolver xmlSourceResolver, final JAXBContext jaxbContext, final IMetadataExtractor metadataExtractor, final IBundleExpander expander) {
		this.scheduler = scheduler;
		this.injector = injector;
		this.xmlSourceResolver = xmlSourceResolver;
		this.jaxbContext = jaxbContext;
		this.metadataExtractor = metadataExtractor;
		this.expander = expander;
	}

	@Override
	public IRenderer createRenderer(IBundle bundle) {
		final FileSystem tmpFs = Jimfs.newFileSystem(Configuration.unix());
		final Path tmpDirectory = tmpFs.getPath("/");
		final IBundle expandedBundle = expander.expandBundle(bundle, tmpDirectory);
		
		return new Renderer(scheduler, injector, xmlSourceResolver,
				jaxbContext, metadataExtractor,
				expandedBundle, tmpDirectory);
	}
}
