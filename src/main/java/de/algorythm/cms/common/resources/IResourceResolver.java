package de.algorythm.cms.common.resources;

import java.io.FileNotFoundException;
import java.net.URI;

public interface IResourceResolver {

	URI toSystemUri(URI absolutePublicUri) throws FileNotFoundException;
	URI toSystemUri(URI publicHref, URI systemBaseUri) throws FileNotFoundException;
}
