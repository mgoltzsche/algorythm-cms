package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.resources.IOutputUriResolver;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final IOutputUriResolver resolver;
	private final IOutputUriResolver tmpResolver;
	
	public CmsOutputURIResolver(final IOutputUriResolver tmpResolver, final IOutputUriResolver resolver) {
		this.tmpResolver = tmpResolver;
		this.resolver = resolver;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI publicUri = baseUri.resolve(href);
		final String scheme = publicUri.getScheme();
		final IOutputUriResolver resolver = scheme != null && "tmp".equals(scheme.toLowerCase())
				? tmpResolver : this.resolver;
		final Path systemPath = resolver.resolveUri(publicUri);
		final Writer writer;
		
		try {
			writer = Files.newBufferedWriter(systemPath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new TransformerException("Cannot write " + systemPath, e);
		}
		
		final Result result = new StreamResult(writer);
		
		result.setSystemId(publicUri.toString());
		
		return result;
	}
	
	@Override
	public CmsOutputURIResolver newInstance() {
		return this;
	}
	
	@Override
	public void close(Result result) throws TransformerException {
	}
}