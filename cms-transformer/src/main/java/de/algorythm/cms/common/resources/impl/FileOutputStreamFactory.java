package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.algorythm.cms.common.resources.IOutputStreamFactory;

public class FileOutputStreamFactory implements IOutputStreamFactory {

	private final Path outputDirectory;

	public FileOutputStreamFactory(final Path outputDirectory) {
		this.outputDirectory = outputDirectory.normalize();
	}

	@Override
	public OutputStream createOutputStream(final String publicPath) {
		final Path path = resolvePath(publicPath);
		
		try {
			Files.createDirectories(path.getParent());
			
			return Files.newOutputStream(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Path resolvePath(final String path) {
		final String relativePath = !path.isEmpty() && path.charAt(0) == '/'
			? path.substring(1)
			: path;
		
		final Path resolvedDirectory = outputDirectory.resolve(relativePath);
		
		if (!resolvedDirectory.toString().startsWith(outputDirectory.toString()))
			throw new IllegalAccessError("Output parent directory access denied: " + path);
		
		return resolvedDirectory;
	}
	
	/*@Override
	public Path resolveUri(final Path publicPath, final Path systemBasePath) {
		final Path systemPath = (publicPath.isAbsolute()
				? localizedDirectory.resolve(ROOT_PATH.relativize(publicPath))
				: systemBasePath.resolveSibling(publicPath)).normalize();
		
		validateSystemPath(systemPath);
		
		return systemPath;
	}
	
	private void validateSystemPath(final Path systemPath) {
		if (!systemPath.startsWith(localizedDirectory))
			throw new IllegalAccessError("Cannot write to " + systemPath + " since it is outside output directory " + localizedDirectory);
	}*/
}
