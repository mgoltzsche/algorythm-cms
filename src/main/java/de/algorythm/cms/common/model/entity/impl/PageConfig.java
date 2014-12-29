package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import de.algorythm.cms.common.model.entity.IPageConfig;

@XmlRootElement(name="page", namespace="http://cms.algorythm.de/common/Bundle")
public class PageConfig implements IPageConfig {

	@XmlAttribute(required = true)
	private String name;
	@XmlAttribute(required = true)
	@XmlSchemaType(name = "anyURI")
	private URI content;
	@XmlAttribute
	private String title;
	@XmlAttribute(name="nav-title")
	private String navigationTitle;
	@XmlAttribute(name="nav-contained")
	private boolean inNavigation;
	@XmlElementRef(type=PageConfig.class)
	private List<IPageConfig> pages = new LinkedList<IPageConfig>();

	public PageConfig() {}

	public PageConfig(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public URI getContent() {
		return content;
	}

	public void setContent(URI content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNavigationTitle() {
		return navigationTitle;
	}

	public void setNavigationTitle(String navigationTitle) {
		this.navigationTitle = navigationTitle;
	}

	public boolean isInNavigation() {
		return inNavigation;
	}

	public void setInNavigation(boolean inNavigation) {
		this.inNavigation = inNavigation;
	}

	@Override
	public List<IPageConfig> getPages() {
		return pages;
	}
	
	public void setPages(final List<IPageConfig> pages) {
		this.pages = pages;
	}
	
	@Override
	public String toString() {
		return "Page [" + name + "]";
	}
}