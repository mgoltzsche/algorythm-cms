package de.algorythm.cms.common.resources;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;

public interface ISourceUriResolver {

	Collection<Path> getRootPathes();
	Path resolve(URI publicUri);
	//Path resolve(Path publicPath, Path systemBasePath);
}
