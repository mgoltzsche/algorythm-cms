package de.algorythm.cms.common.model.entity.impl;

import java.util.Locale;

import de.algorythm.cms.common.model.dao.impl.xml.XmlResourceDao;
import de.algorythm.cms.common.model.entity.ISite;

public class Site extends AbstractPageContainer implements ISite {

	private String title;
	private String contextPath;
	private Locale defaultLocale;

	public Site(final XmlResourceDao dao, final String name, final String title, final Locale defaultLocale, final String contextPath) {
		super(dao, name, "", name);
		this.title = title;
		this.defaultLocale = defaultLocale;
		this.contextPath = contextPath;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
	public String toString() {
		return "Site [" + getName() + "]";
	}
}
