package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.DerivedPageConfig;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.IDestinationPathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

public class PageIndexer implements IRenderingJob {

	@Inject
	private JAXBContext jaxbContext;

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final TimeMeter meter = TimeMeter.meter(ctx.getBundle().getName() + ' ' + this + " initialization");
		final String name = ctx.getBundle().getName();
		final IPageConfig unlocalizedStartPage = ctx.getBundle().getStartPage();
		
		if (unlocalizedStartPage == null)
			throw new IllegalStateException("Missing start page for '" + ctx.getBundle().getName() + "'");
		
		for (ISupportedLocale supportedLocale : ctx.getBundle().getSupportedLocales()) {
			final Locale locale = supportedLocale.getLocale();
			
			ctx.execute(new IRenderingJob() {
				@Override
				public void run(IRenderingContext context) throws Exception {
					final DerivedPageConfig localizedStartPage = deriveLocalizedPage(unlocalizedStartPage, StringUtils.EMPTY, ctx, locale);
					localizedStartPage.setName(name);
					
					for (IPageConfig child : unlocalizedStartPage.getPages()) {
						deriveLocalizedChildren(localizedStartPage, child, ctx, locale);
					}
					
					writePageXml(localizedStartPage, ctx, locale);
				}
			});
		}
		
		meter.finish();
	}
	
	private void writePageXml(final DerivedPageConfig page, final IDestinationPathResolver resolver, final Locale locale) throws Exception {
		final Marshaller marshaller = jaxbContext.createMarshaller();
		final Path pagesXmlFile = resolver.resolveDestination(URI.create("tmp:///" + locale.toLanguageTag() + "/pages.xml"));
		Files.createDirectories(pagesXmlFile.getParent());
		
		try (Writer writer = Files.newBufferedWriter(pagesXmlFile, StandardCharsets.UTF_8)) {
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(page, writer);
		}
	}

	private void deriveLocalizedChildren(final DerivedPageConfig localizedParent, final IPageConfig unlocalizedChild, final IRenderingContext ctx, final Locale locale) throws Exception {
		final String name = unlocalizedChild.getName();
		
		if (name == null || name.isEmpty())
			throw new IllegalStateException("Undefined page name");
		
		final String path = localizedParent.getPath() + '/' + name;
		final DerivedPageConfig derivedPage = deriveLocalizedPage(unlocalizedChild, path, ctx, locale);
		
		localizedParent.getPages().add(derivedPage);
		
		for (IPageConfig child : unlocalizedChild.getPages())
			deriveLocalizedChildren(derivedPage, child, ctx, locale);
	}

	private DerivedPageConfig deriveLocalizedPage(final IPageConfig page, final String path, final IRenderingContext ctx, final Locale locale) throws Exception {
		final IMetadata metadata = extractMetadata(ctx, page, locale);
		
		return new DerivedPageConfig(path, page, metadata);
	}

	private IMetadata extractMetadata(final IRenderingContext ctx, final IPageConfig page, final Locale locale) throws ResourceNotFoundException, MetadataExtractionException {
		try {
			return ctx.extractMetadata(URI.create('/' + locale.toLanguageTag() + page.getSource().getPath()));
		} catch(ResourceNotFoundException e) {
			return ctx.extractMetadata(URI.create(page.getSource().getPath()));
		}
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
