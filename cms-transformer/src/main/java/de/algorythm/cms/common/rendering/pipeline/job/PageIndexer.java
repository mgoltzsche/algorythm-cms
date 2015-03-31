package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.model.entity.bundle.IPage;
import de.algorythm.cms.common.model.entity.impl.DerivedPage;
import de.algorythm.cms.common.rendering.pipeline.IMetadataExtractorProvider;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;

@Singleton
public class PageIndexer {

	private final JAXBContext jaxbContext;
	private final IMetadataExtractor metadataExtractor;

	@Inject
	public PageIndexer(IMetadataExtractorProvider metadataExtractorProvider) throws JAXBException {
		this.jaxbContext = JAXBContext.newInstance(DerivedPage.class);
		this.metadataExtractor = metadataExtractorProvider.getMetadataExtractor();
	}

	public void indexPages(final IPage startPage, final Locale locale, final IRenderingContext ctx) throws Exception {
		final TimeMeter meter = TimeMeter.meter(this + " initialization");
		final DerivedPage localizedStartPage = deriveLocalizedPage(startPage, StringUtils.EMPTY, locale, ctx, ctx.getTmpResources());
		
		localizedStartPage.setName("start-page");
		
		for (IPage child : startPage.getPages())
			deriveLocalizedChildren(localizedStartPage, child, locale, ctx, ctx.getTmpResources());
		
		writePageXml(localizedStartPage, locale, ctx.getTmpResources());
		
		meter.finish();
	}

	private void writePageXml(final DerivedPage page, final Locale locale, final IOutputTargetFactory outputFactory) throws JAXBException, IOException {
		final Marshaller marshaller = jaxbContext.createMarshaller();
		final IOutputTarget target = outputFactory.createOutputTarget('/' + locale.toLanguageTag() + "/pages.xml");
		
		try (OutputStream out = target.createOutputStream()) {
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(page, out);
		}
	}

	private void deriveLocalizedChildren(final DerivedPage localizedParent, final IPage unlocalizedChild, final Locale locale, final IInputResolver resolver, final IWriteableResources tmp) throws ResourceNotFoundException, MetadataExtractionException, IOException {
		final String name = unlocalizedChild.getName();
		
		if (name == null || name.isEmpty())
			throw new IllegalStateException("Undefined page name");
		
		final String path = localizedParent.getPath() + '/' + name;
		final DerivedPage derivedPage = deriveLocalizedPage(unlocalizedChild, path, locale, resolver, tmp);
		
		localizedParent.getPages().add(derivedPage);
		
		for (IPage child : unlocalizedChild.getPages())
			deriveLocalizedChildren(derivedPage, child, locale, resolver, tmp);
	}

	private DerivedPage deriveLocalizedPage(final IPage page, final String path, final Locale locale, final IInputResolver resolver, final IWriteableResources tmp) throws ResourceNotFoundException, MetadataExtractionException, IOException {
		final IMetadata metadata = extractMetadata(page, locale, resolver, tmp);
		
		return new DerivedPage(path, page, metadata);
	}

	private IMetadata extractMetadata(final IPage page, final Locale locale, final IInputResolver resolver, final IWriteableResources tmp) throws ResourceNotFoundException, MetadataExtractionException, IOException {
		try {
			return metadataExtractor.extractMetadata(URI.create('/' + locale.toLanguageTag() + page.getSource().getPath()), resolver, tmp);
		} catch(ResourceNotFoundException e) {
			return metadataExtractor.extractMetadata(URI.create(page.getSource().getPath()), resolver, tmp);
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
