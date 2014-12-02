package de.algorythm.cms.common.resources.impl;

import java.net.URI;

import de.algorythm.cms.common.resources.IOutputUriResolver;

public class OutputUriResolver implements IOutputUriResolver {

	private final URI outputDirectoryUri;
	private final String resourcePrefix;
	
	public OutputUriResolver(final URI outputDirectoryUri, final String resourcePrefix) {
		this.outputDirectoryUri = outputDirectoryUri.normalize();
		final String resourcePrefixWithTrailingSlash = resourcePrefix.charAt(resourcePrefix.length() - 1) == '/' ? resourcePrefix : resourcePrefix + '/';
		this.resourcePrefix = resourcePrefixWithTrailingSlash.substring(1);
	}
	
	@Override
	public URI resolveUri(final URI outputUri) {
		return resolve(outputUri, "");
	}
	
	@Override
	public URI resolveResourceUri(final URI outputUri) {
		return resolve(outputUri, resourcePrefix);
	}
	
	private URI resolve(final URI outputUri, final String prefix) {
		final String path = outputUri.getPath();
		final String relativePath = path.charAt(0) == '/' ? path.substring(1) : path;
		final String relativePathWithPrefix = prefix + relativePath;
		final URI resolvedSystemUri = outputDirectoryUri.resolve(relativePathWithPrefix).normalize();
		
		if (!resolvedSystemUri.toString().startsWith(outputDirectoryUri.toString()))
			throw new IllegalAccessError("Cannot write to " + resolvedSystemUri + " since it is outside output directory " + outputDirectoryUri.getPath());
		
		return resolvedSystemUri;
	}
}
