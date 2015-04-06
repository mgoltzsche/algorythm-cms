package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IInputSource;

public class FileInputSourceResolver implements IInputResolver {

	private final Path rootDirectory;
	private final IInputResolver delegate;

	public FileInputSourceResolver(final Path rootDirectory, final IInputResolver delegate) {
		this.rootDirectory = rootDirectory;
		this.delegate = delegate;
	}

	public FileInputSourceResolver(final Path rootDirectory) {
		this.rootDirectory = rootDirectory;
		this.delegate = IInputResolver.DEFAULT;
	}

	@Override
	public InputStream createInputStream(final URI publicUri) throws IOException {
		final Path file = asPath(publicUri);
		
		return Files.exists(file)
				? Files.newInputStream(asPath(publicUri))
				: delegate.createInputStream(publicUri);
	}

	@Override
	public IInputSource resolveResource(URI publicUri) throws IOException {
		final Path file = asPath(publicUri);
		
		return Files.exists(file)
				? new FileInputSource(file)
				: delegate.resolveResource(publicUri);
	}

	private Path asPath(URI publicUri) {
		final String relativePath = relativize(publicUri.normalize());
		
		return rootDirectory.resolve(relativePath);
	}

	private String relativize(URI publicUri) {
		final String path = publicUri.normalize().getPath();
		final String relativePath = !path.isEmpty() && path.charAt(0) == '/'
			? path.substring(1) : path;
		
		if (relativePath.length() > 2 && relativePath.startsWith("../") || relativePath.equals(".."))
			throw new IllegalArgumentException("Path '" + path + "' is outside root directory");
		
		return relativePath;
	}
}
