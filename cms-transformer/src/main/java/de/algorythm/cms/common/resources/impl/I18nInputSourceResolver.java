package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IInputSource;

public class I18nInputSourceResolver implements IInputResolver {

	static private final URI ROOT = URI.create("/");
	private final IInputResolver delegate;

	public I18nInputSourceResolver(IInputResolver delegate) {
		this.delegate = delegate;
	}

	@Override
	public IInputSource resolveResource(URI publicUri)
			throws IOException {
		publicUri = ROOT.resolve(publicUri).normalize();
		final String publicPath = publicUri.getPath();
		IInputSource source = delegate.resolveResource(publicUri);
		
		if (source == null && publicPath.startsWith("/i18n/")) {
			final URI publicUnlocalizedUri = i18nToGlobalPath(publicUri);
			source = delegate.resolveResource(publicUnlocalizedUri);
		}
		
		return source;
	}

	@Override
	public InputStream createInputStream(URI publicUri)
			throws IOException {
		publicUri = ROOT.resolve(publicUri).normalize();
		final String publicPath = publicUri.getPath();
		InputStream stream = delegate.createInputStream(publicUri);
		
		if (stream == null && publicPath.startsWith("/i18n/")) {
			final URI publicUnlocalizedUri = i18nToGlobalPath(publicUri);
			stream = delegate.createInputStream(publicUnlocalizedUri);
		}
		
		return stream;
	}

	private URI i18nToGlobalPath(URI i18nUri) {
		final String i18nPath = i18nUri.getPath();
		final int nextSlashIndex = i18nPath.indexOf('/', 6);
		final int langTagEndIndex = nextSlashIndex == -1 ? i18nPath.length() : nextSlashIndex;
		
		return URI.create(i18nPath.substring(langTagEndIndex));
	}
}
