package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.DerivedPageConfig;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.ITargetUriResolver;

public class PageIndexer implements IRenderingJob {

	static private final Logger log = LoggerFactory.getLogger(PageIndexer.class);
	
	@Inject
	private XMLInputFactory xmlInputFactory;
	@Inject
	private JAXBContext jaxbContext;

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final TimeMeter meter = TimeMeter.meter(ctx.getBundle().getName() + ' ' + this + " initialization");
		final String name = ctx.getBundle().getName();
		final ISourceUriResolver sourceResolver = ctx.getResourceResolver();
		final ITargetUriResolver targetResolver = ctx.getOutputResolver();
		final IPageConfig unlocalizedStartPage = ctx.getBundle().getStartPage();
		
		if (unlocalizedStartPage == null)
			throw new IllegalStateException("Missing start page for '" + ctx.getBundle().getName() + "'");
		
		for (ISupportedLocale supportedLocale : ctx.getBundle().getSupportedLocales()) {
			final Locale locale = supportedLocale.getLocale();
			
			ctx.execute(new IRenderingJob() {
				@Override
				public void run(IRenderingContext context) throws Exception {
					final DerivedPageConfig localizedStartPage = deriveLocalizedPage(unlocalizedStartPage, StringUtils.EMPTY, sourceResolver, locale);
					localizedStartPage.setName(name);
					
					for (IPageConfig child : unlocalizedStartPage.getPages())
						deriveLocalizedChildren(localizedStartPage, child, sourceResolver, locale);
					
					ctx.setStartPage(locale, localizedStartPage);
					writePageXml(localizedStartPage, targetResolver, locale);
				}
			});
		}
		
		meter.finish();
	}
	
	private void writePageXml(final IPageConfig page, final ITargetUriResolver resolver, final Locale locale) throws Exception {
		final Marshaller marshaller = jaxbContext.createMarshaller();
		final Path pagesXmlFile = resolver.resolveUri(URI.create("tmp:///" + locale.toLanguageTag() + "/pages.xml"));
		Files.createDirectories(pagesXmlFile.getParent());
		
		try (Writer writer = Files.newBufferedWriter(pagesXmlFile, StandardCharsets.UTF_8)) {
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(page, writer);
		}
	}

	private void deriveLocalizedChildren(final IPageConfig localizedParent, final IPageConfig unlocalizedChild, final ISourceUriResolver resolver, final Locale locale) throws Exception {
		final String name = unlocalizedChild.getName();
		
		if (name == null || name.isEmpty())
			throw new IllegalStateException("Undefined page name");
		
		final String parentPath = localizedParent.getPath();
		final IPageConfig localizedPage = deriveLocalizedPage(unlocalizedChild, parentPath + '/' + name, resolver, locale);
		
		localizedParent.getPages().add(localizedPage);
		
		for (IPageConfig child : unlocalizedChild.getPages())
			deriveLocalizedChildren(localizedPage, child, resolver, locale);
	}

	private DerivedPageConfig deriveLocalizedPage(final IPageConfig page, final String path, final ISourceUriResolver resolver, final Locale locale) throws Exception {
		final DerivedPageConfig p = new DerivedPageConfig(page, path);
		Path contentFile;
		
		try {
			contentFile = resolver.resolve(URI.create('/' + locale.toLanguageTag() + page.getContent().getPath()));
		} catch(IllegalStateException e) {
			contentFile = resolver.resolve(URI.create(page.getContent().getPath()));
		}
		
		try (InputStream stream = Files.newInputStream(contentFile)) {
			final XMLEventReader reader = xmlInputFactory.createXMLEventReader(stream);
			
			try {
				while (reader.hasNext()) {
					final XMLEvent evt = reader.nextEvent();
					
					if (evt.isStartElement()) {
						final StartElement element = evt.asStartElement();
						final Attribute attTitle = element.getAttributeByName(new QName("title"));
						final Attribute attNavTitle = element.getAttributeByName(new QName("nav-title"));
						
						if (attTitle != null)
							p.setTitle(attTitle.getValue());
						
						if (attNavTitle != null)
							p.setNavigationTitle(attNavTitle.getValue());
						
						break;
					}
				}
			} finally {
				reader.close();
			}
		}
		
		if (p.getTitle() == null || p.getTitle().isEmpty()) {
			log.warn("Missing page title of " + p.getPath() + " due to undeclared content title in " + p.getContent());
			p.setTitle(p.getName());
		}
		
		if (p.getNavigationTitle() == null)
			p.setNavigationTitle(p.getTitle());
		
		return p;
	}

	@Override
	public int hashCode() {
		return 31 * 1 + getClass().getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() == obj.getClass())
			return true;
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
