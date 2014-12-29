package de.algorythm.cms.common;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

import de.algorythm.cms.common.impl.CmsCommonFacade;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.LocaleInfos;
import de.algorythm.cms.common.model.entity.impl.Metadata;
import de.algorythm.cms.common.model.entity.impl.PageFeed;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.impl.Renderer;
import de.algorythm.cms.common.resources.IBundleExpander;
import de.algorythm.cms.common.resources.IBundleLoader;
import de.algorythm.cms.common.resources.IDependencyLoader;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.impl.BundleExpander;
import de.algorythm.cms.common.resources.impl.BundleLoader;
import de.algorythm.cms.common.resources.impl.ClasspathDependencyLoader;
import de.algorythm.cms.common.resources.impl.DefaultXmlSourceResolver;
import de.algorythm.cms.common.resources.impl.OdtXmlSourceResolver;
import de.algorythm.cms.common.resources.impl.XmlSourceResolverDelegator;
import de.algorythm.cms.common.scheduling.IProcessScheduler;
import de.algorythm.cms.common.scheduling.impl.RoundRobinProcessScheduler;

public class CmsCommonModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			bindICmsCommonFacade(bind(ICmsCommonFacade.class));
			bindConfiguration(bind(Configuration.class));
			bindIProcessScheduler(bind(IProcessScheduler.class));
			bindIRenderer(bind(IRenderer.class));
			bindIBundleLoader(bind(IBundleLoader.class));
			bindIBundleExpander(bind(IBundleExpander.class));
			bindIDependencyLoader(bind(IDependencyLoader.class));
			bindXMLInputFactory(bind(XMLInputFactory.class));
			bindJAXBContext(bind(JAXBContext.class));
			bindIXmlSourceResolver(bind(IXmlSourceResolver.class));
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
	
	protected void bindIProcessScheduler(AnnotatedBindingBuilder<IProcessScheduler> bind) {
		bind.to(RoundRobinProcessScheduler.class);
	}
	
	protected void bindIRenderer(AnnotatedBindingBuilder<IRenderer> bind) {
		bind.to(Renderer.class);
	}
	
	protected void bindIBundleLoader(AnnotatedBindingBuilder<IBundleLoader> bind) {
		bind.to(BundleLoader.class);
	}
	
	protected void bindIBundleExpander(AnnotatedBindingBuilder<IBundleExpander> bind) {
		bind.to(BundleExpander.class);
	}
	
	protected void bindIDependencyLoader(AnnotatedBindingBuilder<IDependencyLoader> bind) {
		bind.to(ClasspathDependencyLoader.class);
	}
	
	protected void bindXMLInputFactory(AnnotatedBindingBuilder<XMLInputFactory> bind) {
		bind.toInstance(XMLInputFactory.newInstance());
	}
	
	protected void bindJAXBContext(AnnotatedBindingBuilder<JAXBContext> bind) {
		try {
			bind.toInstance(JAXBContext.newInstance(Bundle.class, PageFeed.class, LocaleInfos.class, Sources.class, Metadata.class));
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot initialize JAXB context", e);
		}
	}
	
	protected void bindIXmlSourceResolver(AnnotatedBindingBuilder<IXmlSourceResolver> bind) {
		final Map<String, IXmlSourceResolver> extensionMap = new HashMap<String, IXmlSourceResolver>();
		final IXmlSourceResolver xmlResolver = new DefaultXmlSourceResolver();
		
		extensionMap.put("xml", xmlResolver);
		extensionMap.put("xsl", xmlResolver);
		extensionMap.put("xsd", xmlResolver);
		extensionMap.put("svg", xmlResolver);
		extensionMap.put("odt", new OdtXmlSourceResolver());
		bind.toInstance(new XmlSourceResolverDelegator(extensionMap));
	}
}
