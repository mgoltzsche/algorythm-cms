package de.algorythm.cms.common.resources;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Locale;

import de.algorythm.cms.common.model.entity.IBundle;

public interface IResourceResolver {

	URI toSystemUri(URI publicUri) throws FileNotFoundException;
	URI toSystemUri(URI publicHref, URI systemBaseUri) throws FileNotFoundException;
	IResourceResolver createLocalizedResolver(Locale locale);
	IBundle getMergedBundle();
	Collection<URI> getRootPathes();
}
