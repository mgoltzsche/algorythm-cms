package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.algorythm.cms.common.model.entity.IPageConfig;

@XmlRootElement(name="page", namespace="http://cms.algorythm.de/common/Pages")
public class DerivedPageConfig implements IPageConfig {
	
	@XmlAttribute(required = true)
	private String path;
	
	@XmlAttribute(required = true)
	private String name;
	
	@XmlAttribute(required = true)
	private String title;
	
	@XmlAttribute(name="nav-title", required = true)
	private String navigationTitle;
	
	@XmlAttribute(name="nav-contained", required = true)
	private boolean inNavigation = true;
	
	@XmlElementRef(type=DerivedPageConfig.class)
	private List<IPageConfig> pages = new LinkedList<IPageConfig>();
	
	@XmlTransient
	private URI content;

	public DerivedPageConfig() {}
	
	public DerivedPageConfig(final IPageConfig p, final String path) {
		this.path = path;
		name = p.getName();
		content = p.getContent();
		inNavigation = p.isInNavigation();
		title = p.getTitle();
		navigationTitle = p.getNavigationTitle();
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public URI getContent() {
		return content;
	}
	
	public void setContent(URI content) {
		this.content = content;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getNavigationTitle() {
		return navigationTitle;
	}

	public void setNavigationTitle(String navigationTitle) {
		this.navigationTitle = navigationTitle;
	}

	@Override
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

	public void setPages(List<IPageConfig> pages) {
		this.pages = pages;
	}

	@Override
	public String toString() {
		return "DerivedPageConfig [" + path + "/]";
	}
}
