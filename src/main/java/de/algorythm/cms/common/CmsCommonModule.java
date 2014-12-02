package de.algorythm.cms.common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

import de.algorythm.cms.common.impl.CmsCommonFacade;
import de.algorythm.cms.common.impl.xml.XmlReaderFactory;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.model.loader.impl.BundleLoader;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;
import de.algorythm.cms.common.renderer.impl.xml.XmlContentRenderer;
import de.algorythm.cms.common.rendering.pipeline.IRenderer;
import de.algorythm.cms.common.rendering.pipeline.impl.Renderer;
import de.algorythm.cms.common.resources.IDependencyLoader;
import de.algorythm.cms.common.resources.impl.ClasspathDependencyLoader;
import de.algorythm.cms.common.scheduling.IProcessScheduler;
import de.algorythm.cms.common.scheduling.impl.RoundRobinProcessScheduler2;

public class CmsCommonModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			bindICmsCommonFacade(bind(ICmsCommonFacade.class));
			bindConfiguration(bind(Configuration.class));
			bindIProcessScheduler(bind(IProcessScheduler.class));
			bindIBundleLoader(bind(IBundleLoader.class));
			bindIRenderer(bind(IRenderer.class));
			bindIDependencyLoader(bind(IDependencyLoader.class));
			bindIXmlReaderFactory(bind(IXmlReaderFactory.class));
			bindJAXBContext(bind(JAXBContext.class));
			bindIContentRenderer(bind(IContentRenderer.class));
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
		bind.to(RoundRobinProcessScheduler2.class);
	}
	
	protected void bindIBundleLoader(AnnotatedBindingBuilder<IBundleLoader> bind) {
		bind.to(BundleLoader.class);
	}
	
	protected void bindIRenderer(AnnotatedBindingBuilder<IRenderer> bind) {
		bind.to(Renderer.class);
	}
	
	protected void bindIDependencyLoader(AnnotatedBindingBuilder<IDependencyLoader> bind) {
		bind.to(ClasspathDependencyLoader.class);
	}
	
	protected void bindIXmlReaderFactory(AnnotatedBindingBuilder<IXmlReaderFactory> bind) {
		bind.to(XmlReaderFactory.class);
	}
	
	protected void bindJAXBContext(AnnotatedBindingBuilder<JAXBContext> bind) {
		try {
			bind.toInstance(JAXBContext.newInstance(Bundle.class, PageInfo.class));
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot initialize JAXB context", e);
		}
	}
	
	protected void bindIContentRenderer(AnnotatedBindingBuilder<IContentRenderer> bind) {
		bind.to(XmlContentRenderer.class);
	}
}
