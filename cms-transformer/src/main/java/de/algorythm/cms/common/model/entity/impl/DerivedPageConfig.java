package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.model.entity.IPageConfig;

@XmlRootElement(name="page", namespace="http://cms.algorythm.de/common/Pages")
public class DerivedPageConfig {

	@XmlAttribute(required = true)
	private String path;
	@XmlAttribute(required = true)
	private String name;
	@XmlAttribute(required = true)
	@XmlSchemaType(name = "anyURI")
	private URI content;
	@XmlAttribute
	private String title;
	@XmlAttribute(name = "short-title")
	private String shortTitle;
	@XmlAttribute(name="nav-contained")
	private boolean inNavigation;
	@XmlAttribute(name = "created")
	private Date creationTime;
	@XmlAttribute(name = "modified")
	private Date lastModifiedTime;
	@XmlElementRef
	private final List<DerivedPageConfig> pages = new LinkedList<DerivedPageConfig>();

	public DerivedPageConfig() {}
	
	public DerivedPageConfig(final String path, final IPageConfig pageCfg, final IMetadata metadata) {
		this.path = path;
		name = pageCfg.getName();
		content = pageCfg.getSource();
		inNavigation = pageCfg.isInNavigation();
		title = metadata.getTitle();
		shortTitle = metadata.getShortTitle();
		creationTime = metadata.getCreationTime();
		lastModifiedTime = metadata.getLastModifiedTime();
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public boolean isInNavigation() {
		return inNavigation;
	}

	public void setInNavigation(boolean inNavigation) {
		this.inNavigation = inNavigation;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public List<DerivedPageConfig> getPages() {
		return pages;
	}
}
