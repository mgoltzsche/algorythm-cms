package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import de.algorythm.cms.common.resources.IUriResolver;

public class CmsInputURIResolver implements URIResolver {

	private final IUriResolver resolver;
	private final Path notFoundContent;
	
	public CmsInputURIResolver(final IUriResolver resolver, final Path notFoundContent) {
		this.resolver = resolver;
		this.notFoundContent = notFoundContent;
	}
	
	@Override
	public Source resolve(final String href, final String base) throws TransformerException {
		final Path hrefPath = Paths.get(URI.create(href).getPath());
		final Path basePath = Paths.get(URI.create(base).getPath());
		final Reader reader;
		Path filePath;
		
		try {
			filePath = resolver.resolve(hrefPath, basePath);
		} catch(IllegalStateException e) {
			if (notFoundContent == null)
				throw e;
			
			filePath = resolver.resolve(notFoundContent);
		}
		
		try {
			reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new TransformerException("Cannot read content " + filePath, e);
		}
		
		final Source source = new StreamSource(reader);
		
		source.setSystemId(filePath.toString());
		
		return source;
	}
}