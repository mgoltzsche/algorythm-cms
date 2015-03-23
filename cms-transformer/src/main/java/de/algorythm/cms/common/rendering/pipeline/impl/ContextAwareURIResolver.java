package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class ContextAwareURIResolver implements URIResolver {

	private final IRenderingContext ctx;
	private final IXmlSourceResolver xmlSourceResolver;
	
	public ContextAwareURIResolver(final IRenderingContext ctx, final IXmlSourceResolver xmlSourceResolver) {
		this.ctx = ctx;
		this.xmlSourceResolver = xmlSourceResolver;
	}
	
	@Override
	public Source resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		final URI uri = href.isEmpty() ? baseUri : baseUri.resolve(href);
		
		try {
			return xmlSourceResolver.createXmlSource(uri, ctx);
		} catch(ResourceNotFoundException | IOException e) {
			throw new TransformerException(e);
		}
	}
}
