package de.algorythm.cms.common.resources.impl;

import java.net.URI;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.resources.IResourceUriResolver;

public class PageUriResolver implements IResourceUriResolver {

	private final URI repositoryUri;
	private final URI baseUri;

	public PageUriResolver(final Configuration cfg) {
		this.repositoryUri = cfg.repository.toURI().normalize();
		this.baseUri = repositoryUri.resolve("pages/");
	}

	@Override
	public URI toSystemUri(final URI workingSystemUri, final URI publicUri) {
		String publicId = publicUri.getPath().replaceAll("^(.*?)/?(pages\\.xml)?", "$1/pages.xml");
		System.out.println(publicId);
		if (publicId.charAt(0) == '/') { // Absolute URI
			final URI uri = baseUri.resolve(publicId.substring(1)).normalize();
			
			validate(uri);
			
			return uri;
		} else { // Relative URI
			final URI uri = workingSystemUri.resolve(publicId).normalize();
			
			validate(uri);
			
			return uri;
		}
	}
	
	private void validate(final URI systemUri) {
		final URI relativeSystemUri = baseUri.relativize(systemUri);
		
		if (relativeSystemUri.equals(systemUri))
			throw new IllegalArgumentException(systemUri + " must be inside site's page directory " + baseUri);
	}
}
