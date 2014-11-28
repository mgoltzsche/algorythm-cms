package de.algorythm.cms.common.resources;

import java.io.FileNotFoundException;
import java.net.URI;

import de.algorythm.cms.common.model.entity.IBundle;

public interface IResourceResolver {

	URI toSystemUri(URI absolutePublicUri) throws FileNotFoundException;
	URI toSystemUri(URI publicHref, URI systemBaseUri) throws FileNotFoundException;
	IBundle getMergedBundle();
}
