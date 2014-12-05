package de.algorythm.cms.common.resources;

import java.net.URI;
import java.util.Locale;

public interface IOutputUriResolver {

	URI resolveUri(URI publicUri);
	URI resolveResourceUri(URI publicUri);
	IOutputUriResolver createLocalizedResolver(Locale locale);
}
