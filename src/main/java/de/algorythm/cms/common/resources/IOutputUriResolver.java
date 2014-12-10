package de.algorythm.cms.common.resources;

import java.nio.file.Path;
import java.util.Locale;

public interface IOutputUriResolver {

	IOutputUriResolver createLocalizedResolver(Locale locale);
	Path getOutputDirectory();
	Path resolveUri(Path publicPath);
	Path resolveUri(Path publicPath, Path systemBasePath);
}
