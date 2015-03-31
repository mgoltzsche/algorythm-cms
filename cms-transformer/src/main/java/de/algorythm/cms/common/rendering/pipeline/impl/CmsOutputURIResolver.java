package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final IOutputTargetFactory out;
	private final IOutputTargetFactory tmp;
	
	public CmsOutputURIResolver(final IOutputTargetFactory out, final IOutputTargetFactory tmp) {
		this.tmp = tmp;
		this.out = out;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI publicUri = baseUri.resolve(href);
		final String publicPath = publicUri.normalize().getPath();
		final String scheme = publicUri.getScheme();
		final IOutputTargetFactory outFactory = scheme != null &&
				scheme.toLowerCase().equals("tmp")
				? tmp : this.out;
		final IOutputTarget target = outFactory.createOutputTarget(publicPath);
		
		try {
			final Result result = new StreamResult(target.createOutputStream());
			
			result.setSystemId(publicUri.toString());
			
			return result;
		} catch (IOException e) {
			throw new TransformerException(e);
		}
	}
	
	@Override
	public CmsOutputURIResolver newInstance() {
		return this;
	}
	
	@Override
	public void close(Result result) throws TransformerException {
	}
}