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
				
				validateLocalUrl(absoluteUri);
			} else {
				final URI baseUri = new URI(base.indexOf(0) == '/' ? "file:" + base : base);
				System.out.println("### " + baseUri);
				absoluteUri = baseUri.resolve(href).normalize();
				
				if (absoluteUri.getHost() == null)
					validateLocalUrl(absoluteUri);
			}
		} catch(URISyntaxException e) {
			throw new TransformerException("Unsupported URI syntax", e);
		}
		
		return new StreamSource(absoluteUri.toString());
	}
	
	private void validateLocalUrl(final URI absoluteUri) throws TransformerException {
		if (!absoluteUri.getPath().startsWith(rootUri.getPath()))
			throw new TransformerException("Illegal access outside repository: " + absoluteUri);
	}
}
