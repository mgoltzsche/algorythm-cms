package de.algorythm.cms.common.resources.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.Bundle;
import de.algorythm.cms.common.model.entity.impl.SupportedLocale;
import de.algorythm.cms.common.resources.IBundleLoader;

@Singleton
public class BundleLoader implements IBundleLoader {

	private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private final JAXBContext jaxbContext;
	
	@Inject
	public BundleLoader(final JAXBContext jaxbContext) throws JAXBException {
		this.jaxbContext = jaxbContext;
		parserFactory.setNamespaceAware(true);
	}
	
	@Override
	public IBundle getBundle(final Path bundleFile) throws JAXBException {
		if (!Files.exists(bundleFile))
			throw new IllegalArgumentException(bundleFile + " does not exist");
		
		if (!Files.exists(bundleFile))
			throw new IllegalArgumentException(bundleFile + " is a directory");
		
		final Bundle bundle = readBundle(bundleFile);
		
		bundle.setLocation(bundleFile.getParent());
		
		if (bundle.getTitle() == null)
			bundle.setTitle(bundle.getName());
		
		if (bundle.getDefaultLocale() == null)
			bundle.setDefaultLocale(Locale.ENGLISH);
		
		if (bundle.getContextPath() == null)
			bundle.setContextPath("");
		
		final Set<ISupportedLocale> supportedLocales = bundle.getSupportedLocales();
		final Set<ISupportedLocale> mergedSupportedLocales = new LinkedHashSet<ISupportedLocale>(supportedLocales.size() + 1);
		
		mergedSupportedLocales.add(new SupportedLocale(bundle.getDefaultLocale()));
		mergedSupportedLocales.addAll(supportedLocales);
		bundle.setSupportedLocales(mergedSupportedLocales);
		
		return bundle;
	}
	
	private Bundle readBundle(final Path siteCfgFile) throws JAXBException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final Source source = new StreamSource(siteCfgFile.toString());
		
		return unmarshaller.unmarshal(source, Bundle.class).getValue();
	}
}
