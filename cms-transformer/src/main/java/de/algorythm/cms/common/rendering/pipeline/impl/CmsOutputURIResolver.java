package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.OutputStream;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.resources.IOutputStreamFactory;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final IOutputStreamFactory outputStreamFactory;
	private final IOutputStreamFactory tmpOutputStreamFactory;
	
	public CmsOutputURIResolver(final IOutputStreamFactory outputStreamFactory, final IOutputStreamFactory tmpOutputStreamFactory) {
		this.outputStreamFactory = outputStreamFactory;
		this.tmpOutputStreamFactory = tmpOutputStreamFactory;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI publicUri = baseUri.resolve(href);
		final String publicPath = publicUri.normalize().getPath();
		final String scheme = publicUri.getScheme();
		final IOutputStreamFactory outFactory = scheme != null &&
				scheme.toLowerCase().equals("tmp")
				? tmpOutputStreamFactory : outputStreamFactory;
		final OutputStream out = outFactory.createOutputStream(publicPath);
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