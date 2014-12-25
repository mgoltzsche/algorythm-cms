package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.xml.sax.SAXException;

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
		final URI publicUri = baseUri.resolve(href);
		
		try {
			try {
				return ctx.getSource(publicUri, locale);
			} catch(IllegalStateException e) {
				if (notFoundContent == null)
					throw e;
				
				return ctx.getSource(notFoundContent, locale);
			}
		} catch(SAXException | ParserConfigurationException | IOException e) {
			throw new TransformerException("Cannot load " + publicUri, e);
		}
	}
}
