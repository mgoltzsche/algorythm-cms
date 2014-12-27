package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.resources.ITargetUriResolver;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final ITargetUriResolver resolver;
	
	public CmsOutputURIResolver(final ITargetUriResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI publicUri = baseUri.resolve(href);
		final Path outputPath = resolver.resolveUri(publicUri);
		final OutputStream outputStream;
		
		try {
			outputStream = Files.newOutputStream(outputPath);
		} catch (IOException e) {
			throw new TransformerException("Cannot write " + outputPath, e);
		}
		
		final Result result = new StreamResult(outputStream);
		
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