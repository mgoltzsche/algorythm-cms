package de.algorythm.cms.common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

import de.algorythm.cms.common.impl.CmsCommonFacade;
import de.algorythm.cms.common.impl.xml.XmlReaderFactory;
import de.algorythm.cms.common.model.entity.impl.PageInfo;
import de.algorythm.cms.common.model.index.ISiteIndex;
import de.algorythm.cms.common.model.index.impl.XmlSiteIndex;
import de.algorythm.cms.common.renderer.IContentRenderer;
import de.algorythm.cms.common.renderer.impl.xml.IXmlReaderFactory;
import de.algorythm.cms.common.renderer.impl.xml.XmlContentRenderer;

public class CmsCommonModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			bindICmsCommonFacade(bind(ICmsCommonFacade.class));
			bindConfiguration(bind(Configuration.class));
			bindISiteIndex(bind(ISiteIndex.class));
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
	
	protected void bindISiteIndex(AnnotatedBindingBuilder<ISiteIndex> bind) {
		bind.to(XmlSiteIndex.class);
	}
	
	protected void bindIXmlReaderFactory(AnnotatedBindingBuilder<IXmlReaderFactory> bind) {
		bind.to(XmlReaderFactory.class);
	}
	
	protected void bindJAXBContext(AnnotatedBindingBuilder<JAXBContext> bind) {
		try {
			bind.toInstance(JAXBContext.newInstance(PageInfo.class));
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot initialize JAXB context", e);
		}
	}
	
	protected void bindIContentRenderer(AnnotatedBindingBuilder<IContentRenderer> bind) {
		bind.to(XmlContentRenderer.class);
	}
}
