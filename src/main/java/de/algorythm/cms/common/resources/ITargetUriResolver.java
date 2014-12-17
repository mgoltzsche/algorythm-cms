package de.algorythm.cms.common.resources;

import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;

public interface ITargetUriResolver {

	Path getOutputDirectory();
	Path resolveUri(URI publicUri, Locale locale);
	//Path resolveUri(Path publicPath, Path systemBasePath);
}
