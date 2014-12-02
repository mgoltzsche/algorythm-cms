package de.algorythm.cms.common.resources;

import java.net.URI;

public interface IOutputUriResolver {

	URI resolveUri(URI publicUri);
	URI resolveResourceUri(URI publicUri);
}
