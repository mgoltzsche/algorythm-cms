package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IInputSource;

public class ClasspathInputSourceResolver implements IInputResolver {

	static private final Date INIT_DATE = new Date();

	private final IInputResolver delegate;

	public ClasspathInputSourceResolver() {
		this.delegate = IInputResolver.DEFAULT;
	}

	public ClasspathInputSourceResolver(IInputResolver delegate) {
		this.delegate = delegate;
	}

	@Override
	public IInputSource resolveResource(URI publicUri)
			throws IOException {
		final String resourceName = toClasspathResourceName(publicUri);
		
		return getClass().getResource(resourceName) == null
				? delegate.resolveResource(publicUri)
				: new ClasspathInputSource(resourceName, INIT_DATE);
	}

	@Override
	public InputStream createInputStream(URI publicUri)
			throws IOException {
		final String resourceName = toClasspathResourceName(publicUri);
		final InputStream stream = getClass().getResourceAsStream(resourceName);
		
		return stream == null ? delegate.createInputStream(publicUri) : stream;
	}

	private String toClasspathResourceName(final URI uri) {
		final String path = uri.normalize().getPath();
		
		return path.isEmpty() || path.charAt(0) != '/'
				? "/" + path
				: path;
	}
}
