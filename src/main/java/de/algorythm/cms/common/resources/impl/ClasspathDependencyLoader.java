package de.algorythm.cms.common.resources.impl;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.resources.IDependencyLoader;

public class ClasspathDependencyLoader implements IDependencyLoader {

	private final IBundleLoader loader;
	
	@Inject
	public ClasspathDependencyLoader(final IBundleLoader loader) {
		this.loader = loader;
	}
	
	@Override
	public IBundle loadDependency(final String bundleName) {
		final URL bundleFileUrl = getClass().getResource(name2path(bundleName));
		
		if (bundleFileUrl == null)
			throw new IllegalArgumentException("Cannot find bundle " + bundleName);
		
		try {
			return loader.getBundle(new File(bundleFileUrl.toURI()));
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
