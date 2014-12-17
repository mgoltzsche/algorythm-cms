package de.algorythm.cms.common.resources;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

public interface ISourceUriResolver {

	Collection<Path> getRootPathes();
	Path resolve(URI publicUri, Locale locale);
	//Path resolve(Path publicPath, Path systemBasePath);
}
