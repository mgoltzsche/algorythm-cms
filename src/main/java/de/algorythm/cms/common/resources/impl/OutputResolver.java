package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.nio.file.Path;

import de.algorythm.cms.common.resources.ITargetUriResolver;

public class OutputResolver implements ITargetUriResolver {

	//static private final Path ROOT_PATH = Paths.get("/");
	
	private final Path outputDirectory;
	private final Path tmpDirectory;

	public OutputResolver(final Path outputDirectory, final Path tmpDirectory) {
		this.outputDirectory = outputDirectory.normalize();
		this.tmpDirectory = tmpDirectory.normalize();
	}

	@Override
	public Path getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	public Path resolveUri(final URI publicUri) {
		final String scheme = publicUri.getScheme();
		final Path directory = scheme != null && scheme.toLowerCase().equals("tmp")
				? tmpDirectory : outputDirectory;
		final String path = publicUri.normalize().getPath();
		final String relativePath = !path.isEmpty() && path.charAt(0) == '/'
			? path.substring(1)
			: path;
		
		final Path resolvedDirectory = directory.resolve(relativePath);
		
		if (!resolvedDirectory.toString().startsWith(directory.toString()))
			throw new IllegalAccessError("Output parent directory access denied: " + publicUri);
		
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
