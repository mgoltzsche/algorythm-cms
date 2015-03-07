package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IBundleLoader;
import de.algorythm.cms.common.resources.IDependencyLoader;

@Singleton
public class ClasspathDependencyLoader implements IDependencyLoader {

	private final IBundleLoader loader;
	private final ZipArchiveUtil zipUtil;
	
	@Inject
	public ClasspathDependencyLoader(IBundleLoader loader, ZipArchiveUtil zipUtil) {
		this.loader = loader;
		this.zipUtil = zipUtil;
	}
	
	@Override
	public IBundle loadDependency(final String bundleName, final Path tmpDirectory) {
		final String path = name2path(bundleName);
		final URL bundleFileUrl = getClass().getResource(path);
		
		if (bundleFileUrl == null)
			throw new IllegalArgumentException("Cannot find bundle '" + bundleName + '\'');
		
		try {
			final Path bundleFilePath = bundleFileUrl.getProtocol().equals("jar")
					? extractedPath(bundleFileUrl.getPath().substring(5), tmpDirectory)
					: Paths.get(bundleFileUrl.toURI());
			
			//final FileSystem fs = FileSystems.newFileSystem(Paths.get("zip:/home/max/development/java/algorythm-cms/target/algorythm-cms-jar-with-dependencies.jar"), null);
			//return loader.getBundle(fs.getPath(path)); // same like next line
			return loader.getBundle(bundleFilePath);
		} catch(Exception e) {
			throw new IllegalStateException("Cannot load bundle '" + bundleName + '\'', e);
		}
	}
	
	private String name2path(final String bundleName) {
		if (!bundleName.matches("[\\w\\._\\d]+"))
			throw new IllegalArgumentException("Unsupported dependency name: " + bundleName);
		
		final String[] depNameSegments = bundleName.split("\\.");
		final StringBuilder depPath = new StringBuilder();
		
		for (int i = depNameSegments.length - 1; i >= 0; i--)
			depPath.append('/').append(depNameSegments[i]);
		
		return depPath.append("/bundle.xml").toString();
	}
	
	private Path extractedPath(final String filePath, final Path tmpDirectory) throws IOException {
		final Path destinationDirectory = tmpDirectory.resolve(String.valueOf(System.currentTimeMillis()));
		final int fsSeparatorPos = filePath.indexOf('!');
		final String zipPath = filePath.substring(0, fsSeparatorPos);
		final String zipRelativePath = filePath.substring(fsSeparatorPos + 2);
		final Path zipFile = Paths.get(URI.create("file:" + zipPath));
		final Path resolvedFile = destinationDirectory.resolve(zipRelativePath);
		
		zipUtil.unzip(zipFile, destinationDirectory);
		
		return resolvedFile;
	}
}
