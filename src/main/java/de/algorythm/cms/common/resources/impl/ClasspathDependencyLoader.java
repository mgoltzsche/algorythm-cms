package de.algorythm.cms.common.resources.impl;

import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.resources.IBundleLoader;
import de.algorythm.cms.common.resources.IDependencyLoader;

@Singleton
public class ClasspathDependencyLoader implements IDependencyLoader {

	private final IBundleLoader loader;
	
	@Inject
	public ClasspathDependencyLoader(final IBundleLoader loader) {
		this.loader = loader;
	}
	
	@Override
	public IBundle loadDependency(final String bundleName) {
		final String path = name2path(bundleName);
		final URL bundleFileUrl = getClass().getResource(path);
		
		if (bundleFileUrl == null)
			throw new IllegalArgumentException("Cannot find bundle '" + bundleName + '\'');
		
		try {
			//final FileSystem fs = FileSystems.newFileSystem(Paths.get("zip:/home/max/development/java/algorythm-cms/target/algorythm-cms-jar-with-dependencies.jar"), null);
			//return loader.getBundle(fs.getPath(path)); // same like next line
			return loader.getBundle(Paths.get(bundleFileUrl.toURI()));
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
}
