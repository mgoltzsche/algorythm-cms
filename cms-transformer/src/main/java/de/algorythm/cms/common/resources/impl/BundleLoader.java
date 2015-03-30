package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import de.algorythm.cms.common.impl.jaxb.adapter.UriXmlAdapter;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.impl.bundle.Bundle;
import de.algorythm.cms.common.resources.IBundleLoader;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

@Singleton
public class BundleLoader implements IBundleLoader {

	private final JAXBContext jaxbContext;

	public BundleLoader() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(Bundle.class);
	}

	public IBundle loadBundle(URI publicUri, final ISourcePathResolver resolver) throws ResourceNotFoundException, IOException, JAXBException {
		publicUri = publicUri.normalize();
		final Path privatePath = resolver.resolveSource(publicUri);
		
		if (Files.isDirectory(privatePath))
			throw new IllegalArgumentException(privatePath + " is a directory");
		
		final Bundle bundle = readBundle(publicUri, privatePath);
		
		if (bundle.getTitle() == null || bundle.getTitle().isEmpty())
			throw new IllegalArgumentException("Missing title attribute of bundle " + publicUri);
		
		if (bundle.getDefaultLocale() == null)
			throw new IllegalArgumentException("Missing default-locale attribute of bundle " + publicUri);
		
		bundle.setUri(publicUri);
		bundle.getSupportedLocales().add(bundle.getDefaultLocale());
		
		return bundle;
	}

	private Bundle readBundle(final URI publicUri, final Path privatePath) throws JAXBException, IOException {
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		unmarshaller.setAdapter(UriXmlAdapter.class, new UriXmlAdapter(publicUri));
		
		try (InputStream stream = Files.newInputStream(privatePath)) {
			final Source source = new StreamSource(stream);
			
			return unmarshaller.unmarshal(source, Bundle.class).getValue();
		}
	}
}
