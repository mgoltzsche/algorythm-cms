
package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import de.algorythm.cms.common.resources.ISourceUriResolver;

public class CmsTemplateURIResolver implements URIResolver {

	private final ISourceUriResolver resolver;
	
	public CmsTemplateURIResolver(final ISourceUriResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public Source resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI publicUri = baseUri.resolve(href);
		final Path filePath = resolver.resolve(publicUri, Locale.ROOT);
		final InputStream stream;
		
		try {
			stream = Files.newInputStream(filePath);
		} catch (IOException e) {
			throw new TransformerException("Cannot read file " + filePath, e);
		}
		
		return new StreamSource(stream, publicUri.toString());
	}
}
