package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.resources.IOutputStreamFactory;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final IOutputStreamFactory outputStreamFactory;
	
	public CmsOutputURIResolver(final IOutputStreamFactory outputStreamFactory) {
		this.outputStreamFactory = outputStreamFactory;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI publicUri = baseUri.resolve(href);
		final OutputStream out = outputStreamFactory.createOutputStream(publicUri);
		final Result result = new StreamResult(out);
		
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