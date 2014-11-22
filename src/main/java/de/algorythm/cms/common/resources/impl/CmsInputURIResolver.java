package de.algorythm.cms.common.resources.impl;

import java.io.FileNotFoundException;
import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import de.algorythm.cms.common.resources.IResourceResolver;

public class CmsInputURIResolver implements URIResolver {

	private final IResourceResolver uriResolver;
	private final String scheme;
	
	public CmsInputURIResolver(final IResourceResolver uriResolver, final String scheme) {
		this.uriResolver = uriResolver;
		this.scheme = scheme;
	}
	
	@Override
	public Source resolve(final String href, final String base) throws TransformerException {
		try {
			return new StreamSource(uriResolver.toSystemUri(URI.create(href), URI.create(base)).toString());
		} catch (FileNotFoundException e) {
			throw new TransformerException(e);
		}
	}
}
