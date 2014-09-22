package de.algorythm.cms.common.resources.impl;

import java.net.URI;

import javax.inject.Singleton;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.resources.IResourceUriResolver;

@Singleton
public class ContentUriResolver implements IResourceUriResolver {

	private final URI repositoryUri;

	public ContentUriResolver(final Configuration cfg) {
		this.repositoryUri = cfg.repository.toURI().normalize();
	}

	@Override
	public URI toSystemUri(final URI workingSystemUri, final URI publicUri) {
		final URI repoRelativeUri = toRepositoryRelativeUri(workingSystemUri);
		final String publicId = publicUri.getPath();
		
		// TODO: normalize resulting URI
		
		if (publicId.charAt(0) == '/') // Absolute URI
			return repositoryUri.resolve(publicId
					.replaceAll("^/([^/]+)/(.*)$", "$1/contents/$2"));
		else if (repoRelativeUri.getPath().matches("^[^/]+/contents/.*"))
			return workingSystemUri.resolve(publicId);
		else {
			final String siteName = repoRelativeUri.getPath().replaceAll("^([^/]+)/.*", "$1");
			final URI relativeSystemUri = repositoryUri.resolve(siteName + "/contents/" + publicUri);
			final String relativeContentSystemUri = relativeSystemUri.getPath().replaceAll("^([^/]+)/[^/]+/(.*)$", "$1/contents/$2");
			
			return repositoryUri.resolve(relativeContentSystemUri);
		}
	}
	
	private URI toRepositoryRelativeUri(URI systemUri) {
		systemUri = systemUri.normalize();
		final URI relativeSystemUri = repositoryUri.relativize(systemUri);
		
		if (relativeSystemUri.equals(systemUri))
			throw new IllegalArgumentException(systemUri + " must be inside repository directory " + repositoryUri);
		
		return relativeSystemUri;
	}
}
