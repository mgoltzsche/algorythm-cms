package de.algorythm.cms.common.model.entity.bundle;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.algorythm.cms.common.model.entity.impl.Page;

public interface IBundle {

	String getTitle();
	URI getUri();
	Locale getDefaultLocale();
	Set<Locale> getSupportedLocales();
	Set<URI> getDependencies();
	Map<OutputFormat, IOutputConfig> getOutputMapping();
	Page getStartPage();
}
