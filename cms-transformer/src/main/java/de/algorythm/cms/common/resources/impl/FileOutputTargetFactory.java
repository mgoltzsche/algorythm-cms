package de.algorythm.cms.common.resources.impl;

import java.nio.file.Path;

import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IWriteableResources;

public class FileOutputTargetFactory implements IWriteableResources {

	protected final Path outputDirectory;

	public FileOutputTargetFactory(final Path outputDirectory) {
		this.outputDirectory = outputDirectory.normalize();
	}

	@Override
	public IOutputTarget createOutputTarget(final String publicPath) {
		return new FileOutputTarget(publicPath, resolvePublicPath(publicPath));
	}

	@Override
	public Path resolvePublicPath(final String path) {
		final String relativePath = !path.isEmpty() && path.charAt(0) == '/'
			? path.substring(1) : path;
		
		if (relativePath.length() > 2 && relativePath.startsWith("../") || relativePath.equals(".."))
			throw new IllegalArgumentException("Path '" + path + "' is outside root directory");
		
		return outputDirectory.resolve(relativePath);
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
