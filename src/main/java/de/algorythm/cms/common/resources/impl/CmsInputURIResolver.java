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
	
	public CmsInputURIResolver(final IResourceResolver uriResolver) {
		this.uriResolver = uriResolver;
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
