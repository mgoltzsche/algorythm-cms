package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.algorythm.cms.common.resources.IOutputUriResolver;

public class OutputUriResolver implements IOutputUriResolver {

	private final URI outputDirectoryUri;
	private final String localePrefix;

	public OutputUriResolver(final URI outputDirectoryUri) {
		this(outputDirectoryUri, StringUtils.EMPTY);
	}

	private OutputUriResolver(final URI outputDirectoryUri, final String localePrefix) {
		this.localePrefix = localePrefix;
		this.outputDirectoryUri = outputDirectoryUri.normalize();
	}

	@Override
	public IOutputUriResolver createLocalizedResolver(final Locale locale) {
		return new OutputUriResolver(outputDirectoryUri, locale.getLanguage() + '/');
	}

	@Override
	public URI resolveUri(final URI outputUri) {
		final String path = outputUri.getPath();
		final String relativePath = path.charAt(0) == '/' ? path.substring(1) : path;
		final String relativePathWithPrefix = localePrefix + relativePath;
		final URI systemUri = outputDirectoryUri.resolve(relativePathWithPrefix).normalize();
		
		validateSystemUri(systemUri);
		
		return systemUri;
	}

	@Override
	public URI resolveUri(final URI publicUri, final URI systemBaseUri) {
		final String path = publicUri.getPath();
		final URI systemUri = (path.isEmpty() || path.charAt(0) != '/'
				? systemBaseUri.resolve(path)
				: outputDirectoryUri.resolve(path.substring(1))).normalize();
		
		System.out.println(publicUri + "  "  + systemBaseUri + " -> " + systemUri);
		validateSystemUri(systemUri);
		
		return systemUri;
	}
	
	private void validateSystemUri(final URI systemUri) {
		if (!systemUri.toString().startsWith(outputDirectoryUri.toString()))
			throw new IllegalAccessError("Cannot write to " + systemUri + " since it is outside output directory " + outputDirectoryUri.getPath());
	}
}
