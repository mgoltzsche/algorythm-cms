package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.impl.bundle.Bundle;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

@Singleton
public class BundleLoader2 {

	private final JAXBContext jaxbContext;

	@Inject
	public BundleLoader2(final JAXBContext jaxbContext) throws JAXBException {
		this.jaxbContext = jaxbContext;
	}

	public IBundle getBundle(URI publicUri, final ISourcePathResolver resolver) throws ResourceNotFoundException, IOException, JAXBException {
		publicUri = publicUri.normalize();
		final Path bundleFile = resolver.resolveSource(publicUri);
		
		if (Files.isDirectory(bundleFile))
			throw new IllegalArgumentException(bundleFile + " is a directory");
		
		final Bundle bundle = readBundle(bundleFile);
		
		bundle.setUri(publicUri);
		
		if (bundle.getTitle() == null || bundle.getTitle().isEmpty())
			throw new IllegalArgumentException("Missing title in bundle " + publicUri);
		
		// TODO: supported locales
		
		return bundle;
	}

	private Bundle readBundle(final Path siteCfgFile) throws JAXBException, IOException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final InputStream cfgStream = Files.newInputStream(siteCfgFile);
		final Source source = new StreamSource(cfgStream);
		
		return unmarshaller.unmarshal(source, Bundle.class).getValue();
	}
}
