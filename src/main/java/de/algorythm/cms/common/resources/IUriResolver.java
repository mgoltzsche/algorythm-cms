package de.algorythm.cms.common.resources;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

public interface IUriResolver {

	IUriResolver createLocalizedResolver(Locale locale);
	Collection<Path> getRootPathes();
	Path resolve(URI publicUri);
	//Path resolve(Path publicPath, Path systemBasePath);
}
