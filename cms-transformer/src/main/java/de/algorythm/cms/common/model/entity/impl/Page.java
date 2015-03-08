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
public class Page implements IPageConfig {

	@XmlAttribute(required = true)
	private String name;
	@XmlAttribute(name = "src", required = true)
	@XmlSchemaType(name = "anyURI")
	private URI source;
	@XmlAttribute
	private String title;
	@XmlAttribute(name="nav-title")
	private String navigationTitle;
	@XmlAttribute(name="nav-contained")
	private boolean inNavigation = true;
	@XmlElementRef(type=Page.class)
	private List<IPageConfig> pages = new LinkedList<IPageConfig>();

	public Page() {}

	public Page(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public URI getSource() {
		return source;
	}

	public void setContent(URI source) {
		this.source = source;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Page other = (Page) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Page [" + name + "]";
	}
}