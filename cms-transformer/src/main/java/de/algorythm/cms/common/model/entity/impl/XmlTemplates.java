package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;
import java.util.List;

public class XmlTemplates {

	private final List<URI> templateUris;
	private final URI themeTemplateUri;
	
	public XmlTemplates(final List<URI> templateUris, final URI themeTemplateUri) {
		this.templateUris = templateUris;
		this.themeTemplateUri = themeTemplateUri;
	}

	public List<URI> getTemplateUris() {
		return templateUris;
	}

	public URI getThemeTemplateUri() {
		return themeTemplateUri;
	}
}
