package de.algorythm.cms.common.resources;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

public interface IUriResolver {

	IUriResolver createLocalizedResolver(Locale locale);
	Collection<Path> getRootPathes();
	Path resolve(Path publicPath);
	Path resolve(Path publicPath, Path systemBasePath);
}
