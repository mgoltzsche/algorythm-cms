package de.algorythm.cms.common.resources;

import java.net.URI;

public interface IResourceUriResolver {

	URI toSystemUri(URI workingSystemUri, URI publicUri);
}
