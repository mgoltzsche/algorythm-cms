package de.algorythm.cms.common.resources.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final URI rootUri;
	private final URI baseUri;
	
	public CmsOutputURIResolver(final URI outputRootUri, final URI baseUri) {
		this.rootUri = outputRootUri;
		this.baseUri = baseUri;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final URI absoluteUri;
		
		try {
			if (href.charAt(0) == '/') { // Absolute
				absoluteUri = new URI(rootUri + href).normalize();
			} else if (href.matches("^\\w+:/.*")) {
				final URI hrefUri = new URI(href);
				absoluteUri = new URI(rootUri + hrefUri.getPath());
			} else {
				absoluteUri = baseUri.resolve(href).normalize();
			}
		} catch(URISyntaxException e) {
			throw new TransformerException("Unsupported URI syntax", e);
		}
		
		validateWithinRootUri(absoluteUri);
		
		return new StreamResult(new File(absoluteUri)); // TODO: close file?!
	}
	
	private void validateWithinRootUri(final URI absoluteUri) throws TransformerException {
		if (!absoluteUri.getPath().startsWith(rootUri.getPath()))
			throw new TransformerException("URI " + absoluteUri + " is outside output URI " + rootUri);
	}
	
	@Override
	public CmsOutputURIResolver newInstance() {
		return this;
	}
	
	@Override
	public void close(Result result) throws TransformerException {
	}
}
