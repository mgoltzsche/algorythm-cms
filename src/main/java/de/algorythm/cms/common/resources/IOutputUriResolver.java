package de.algorythm.cms.common.resources;

import java.net.URI;
import java.util.Locale;

public interface IOutputUriResolver {

	URI resolveUri(URI publicUri);
	URI resolveUri(URI publicUri, URI systemBaseUri);
	IOutputUriResolver createLocalizedResolver(Locale locale);
}
