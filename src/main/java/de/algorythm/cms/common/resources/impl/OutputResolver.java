package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;

import de.algorythm.cms.common.resources.IOutputUriResolver;

public class OutputResolver implements IOutputUriResolver {

	//static private final Path ROOT_PATH = Paths.get("/");
	
	private final Path directory;
	private final Path localizedDirectory;

	public OutputResolver(final Path directory) {
		this.localizedDirectory = this.directory = directory.normalize();
	}

	private OutputResolver(final Path directory, final Locale locale) {
		this.directory = directory;
		this.localizedDirectory = directory.resolve(locale.getLanguage());
	}

	@Override
	public Path getOutputDirectory() {
		return localizedDirectory;
	}

	@Override
	public IOutputUriResolver createLocalizedResolver(final Locale locale) {
		return new OutputResolver(directory, locale);
	}

	@Override
	public Path resolveUri(final URI publicUri) {
		final String path = publicUri.normalize().getPath();
		final String relativePath = !path.isEmpty() && path.charAt(0) == '/'
			? path.substring(1)
			: path;
		
		if (relativePath.length() > 2 && relativePath.startsWith("../") ||
				relativePath.startsWith(".."))
			throw new IllegalAccessError("Output parent directory access denied: " + publicUri);
		
		return localizedDirectory.resolve(relativePath);
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
