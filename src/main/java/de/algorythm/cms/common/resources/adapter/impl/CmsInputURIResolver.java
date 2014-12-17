package de.algorythm.cms.common.resources.adapter.impl;

import java.net.URI;
import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;

public class CmsInputURIResolver implements URIResolver {

	private final IBundleRenderingContext ctx;
	private final URI notFoundContent;
	private final Locale locale;
	
	public CmsInputURIResolver(final IBundleRenderingContext ctx, final Locale locale, final URI notFoundContent) {
		this.ctx = ctx;
		this.locale = locale;
		this.notFoundContent = notFoundContent;
	}
	
	@Override
	public Source resolve(final String href, final String base) throws TransformerException {
		final URI baseUri = URI.create(base);
		URI publicUri = baseUri.resolve(href);
		Node document;
		
		try {
			document = ctx.getDocument(publicUri, locale);
		} catch(IllegalStateException e) {
			if (notFoundContent == null)
				throw e;
			
			document = ctx.getDocument(notFoundContent, locale);
			publicUri = notFoundContent;
		}
		
		return new DOMSource(document, publicUri.toString());
	}
}
