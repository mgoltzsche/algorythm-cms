package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IInputSource;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class FileInputSourceResolver implements IInputResolver {

	private final List<Path> rootPathes;
	private final IInputResolver delegate;

	public FileInputSourceResolver(final List<Path> locations, final IInputResolver delegate) {
		this.rootPathes = locations;
		this.delegate = delegate;
	}
	
	public FileInputSourceResolver(final List<Path> locations) {
		this.rootPathes = locations;
		this.delegate = IInputResolver.DEFAULT;
	}

	@Override
	public InputStream createInputStream(final URI publicUri) throws ResourceNotFoundException, IOException {
		final Path file = asPath(publicUri);
		
		return file == null
				? delegate.createInputStream(publicUri)
				: Files.newInputStream(asPath(publicUri));
	}

	@Override
	public IInputSource resolveResource(URI publicUri) throws ResourceNotFoundException, IOException {
		final Path file = asPath(publicUri);
		
		return file == null
				? delegate.resolveResource(publicUri)
				: new FileInputSource(file);
	}

	private Path asPath(URI publicUri) {
		final String relativePath = relativize(publicUri.normalize());
		
		for (Path rootPath : rootPathes) {
			final Path file = rootPath.resolve(relativePath);
			
			if (Files.exists(file))
				return file;
		}
		
		return null;
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
