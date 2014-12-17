
package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
		final Reader reader;
		
		try {
			reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new TransformerException("Cannot read content " + filePath, e);
		}
		
		final Source source = new StreamSource(reader);
		
		source.setSystemId(publicUri.toString());
		
		return source;
	}
}
