package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class CmsURIResolver implements URIResolver {

	private final URI rootUri;
	
	public CmsURIResolver(final URI rootUri) {
		this.rootUri = rootUri;
	}
	
	@Override
	public Source resolve(String href, final String base) throws TransformerException {
		final URI absoluteUri;
		
		try {
			if (href.charAt(0) == '/') { // Absolute
				absoluteUri = new URI(rootUri + href).normalize();
			} else if (href.matches("^\\w+:/.*")) {
				final URI hrefUri = new URI(href);
				absoluteUri = new URI(rootUri + hrefUri.getPath());
			} else {
				final URI baseUri = new URI(base.indexOf(0) == '/' ? "file:" + base : base);
				absoluteUri = baseUri.resolve(href).normalize();
			}
		} catch(URISyntaxException e) {
			throw new TransformerException("Unsupported URI syntax", e);
		}
		
		validateLocalUrl(absoluteUri);
		
		return new StreamSource(absoluteUri.toString());
	}
	
	private void validateLocalUrl(final URI absoluteUri) throws TransformerException {
		if (!absoluteUri.getPath().startsWith(rootUri.getPath()))
			throw new TransformerException("Illegal access outside repository: " + absoluteUri);
	}
}
