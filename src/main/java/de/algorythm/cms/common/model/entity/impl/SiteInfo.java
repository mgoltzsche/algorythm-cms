package de.algorythm.cms.common.model.entity.impl;

import java.util.Locale;

import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;

public class SiteInfo implements ISite {

	private String name;
	private String title;
	private String description;
	private String contextPath;
	private Locale defaultLocale;
	private String defaultTemplate;
	private IPage startPage;

	public SiteInfo(final String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	
	@Override
	public String getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(String defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	@Override
	public IPage getStartPage() {
		return startPage;
	}

	public void setStartPage(IPage startPage) {
		this.startPage = startPage;
	}

	@Override
	public int compareTo(final ISite site) {
		return name.compareTo(site.getName());
	}

	@Override
	public String toString() {
		return "Site [" + getName() + "]";
	}
}
