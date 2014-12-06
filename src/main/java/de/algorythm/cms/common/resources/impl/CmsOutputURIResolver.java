package de.algorythm.cms.common.resources.impl;

import java.io.File;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.resources.IOutputUriResolver;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final IOutputUriResolver resolver;
	
	public CmsOutputURIResolver(final IOutputUriResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final URI publicUri = URI.create(href);
		final URI systemBaseUri = URI.create(href);
		final URI systemUri = resolver.resolveUri(publicUri, systemBaseUri);
		
		return new StreamResult(new File(systemUri)); // TODO: close file?!
	}
	
	@Override
	public CmsOutputURIResolver newInstance() {
		return this;
	}
	
	@Override
	public void close(Result result) throws TransformerException {
	}
}
