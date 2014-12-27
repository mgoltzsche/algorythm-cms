package de.algorythm.cms.common.resources;

import java.net.URI;
import java.nio.file.Path;

public interface ITargetUriResolver {

	Path getOutputDirectory();
	Path resolveUri(URI publicUri);
	//Path resolveUri(Path publicPath, Path systemBasePath);
}
