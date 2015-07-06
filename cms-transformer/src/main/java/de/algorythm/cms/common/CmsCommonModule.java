package de.algorythm.cms.common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

import de.algorythm.cms.common.impl.CmsCommonFacade;
import de.algorythm.cms.common.impl.RendererFactory;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.DerivedPage;
import de.algorythm.cms.common.model.entity.impl.LocaleInfos;
import de.algorythm.cms.common.model.entity.impl.Metadata;
import de.algorythm.cms.common.model.entity.impl.PageFeed;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IMetadataExtractorProvider;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.rendering.pipeline.IXmlSourceResolverProvider;
import de.algorythm.cms.common.rendering.pipeline.impl.MetadataExtractorFactory;
import de.algorythm.cms.common.rendering.pipeline.impl.XmlFactory;
import de.algorythm.cms.common.resources.IArchiveExtractor;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.resources.IBundleLoader;
import de.algorythm.cms.common.resources.impl.BundleExpander;
import de.algorythm.cms.common.resources.impl.BundleLoaderFileSystem;
import de.algorythm.cms.common.resources.impl.SynchronizedZipArchiveExtractor;
import de.algorythm.cms.common.resources.impl.XmlSourceResolverProvider;
import de.algorythm.cms.common.scheduling.IProcessScheduler;
import de.algorythm.cms.common.scheduling.impl.RoundRobinProcessScheduler;

public class CmsCommonModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			bindICmsCommonFacade(bind(ICmsCommonFacade.class));
			bindConfiguration(bind(Configuration.class));
			bindIRendererFactory(bind(IRendererFactory.class));
			bindIProcessScheduler(bind(IProcessScheduler.class));
			bindIBundleLoader(bind(IBundleLoader.class));
			bindIBundleExpander(bind(IBundleExpander.class));
			bindXMLInputFactory(bind(XMLInputFactory.class));
			bindJAXBContext(bind(JAXBContext.class));
			bindIXmlSourceResolverProvider(bind(IXmlSourceResolverProvider.class));
			bindIMetadataExtractorProvider(bind(IMetadataExtractorProvider.class));
			bindIXmlFactory(bind(IXmlFactory.class));
			bindIArchiveExtractor(bind(IArchiveExtractor.class));
		} catch(Exception e) {
			throw new RuntimeException("Cannot initialize module", e);
		}
	}
	
	protected void bindICmsCommonFacade(AnnotatedBindingBuilder<ICmsCommonFacade> bind) {
		bind.to(CmsCommonFacade.class);
	}
	
	protected void bindConfiguration(AnnotatedBindingBuilder<Configuration> bind) {
		bind.toInstance(new Configuration());
	}
	
	protected void bindIRendererFactory(AnnotatedBindingBuilder<IRendererFactory> bind) {
		bind.to(RendererFactory.class);
	}
	
	protected void bindIProcessScheduler(AnnotatedBindingBuilder<IProcessScheduler> bind) {
		bind.to(RoundRobinProcessScheduler.class);
	}
	
	protected void bindIBundleLoader(AnnotatedBindingBuilder<IBundleLoader> bind) {
		bind.to(BundleLoaderFileSystem.class);
	}
	
	protected void bindIBundleExpander(AnnotatedBindingBuilder<IBundleExpander> bind) {
		bind.to(BundleExpander.class);
	}
	
	protected void bindXMLInputFactory(AnnotatedBindingBuilder<XMLInputFactory> bind) {
		bind.toInstance(XMLInputFactory.newInstance());
	}
	
	protected void bindJAXBContext(AnnotatedBindingBuilder<JAXBContext> bind) {
		try {
			bind.toInstance(JAXBContext.newInstance(Bundle.class, PageFeed.class, LocaleInfos.class, Sources.class, Metadata.class, DerivedPage.class));
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot initialize JAXB context", e);
		}
	}
	
	protected void bindIXmlSourceResolverProvider(AnnotatedBindingBuilder<IXmlSourceResolverProvider> bind) {
		bind.to(XmlSourceResolverProvider.class);
	}
	
	protected void bindIMetadataExtractorProvider(AnnotatedBindingBuilder<IMetadataExtractorProvider> bind) {
		bind.to(MetadataExtractorFactory.class);
	}
	
	protected void bindIXmlFactory(AnnotatedBindingBuilder<IXmlFactory> bind) {
		bind.to(XmlFactory.class);
	}
	
	protected void bindIArchiveExtractor(AnnotatedBindingBuilder<IArchiveExtractor> bind) {
		bind.to(SynchronizedZipArchiveExtractor.class);
	}
}
