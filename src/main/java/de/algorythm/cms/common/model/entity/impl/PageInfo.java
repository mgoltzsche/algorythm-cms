package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;

import de.algorythm.cms.common.model.entity.IPage;

@XmlRootElement(name="page", namespace="http://cms.algorythm.de/common/Pages")
public class PageInfo implements IPage, Comparable<IPage> {
	
	@XmlAttribute(required = true)
	private String path;
	@XmlTransient
	private String name;
	@XmlTransient
	private String title = StringUtils.EMPTY;
	@XmlAttribute(name="title", required = true)
	private String navigationTitle;
	@XmlAttribute(name="in-navigation")
	private boolean inNavigation;
	@XmlElementRef(type=PageInfo.class)
	private List<IPage> pages = new LinkedList<IPage>();
	
	public PageInfo() {}
	
	public PageInfo(final String path, final String name) {
		this.name = name;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getPath() {
		return path;
	}
	
	public void setPath(final String path) {
		this.path = path;
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
	public List<IPage> getPages() {
		return pages;
	}
	
	public void setPages(final List<IPage> pages) {
		this.pages = pages;
	}
	
	@Override
	public int compareTo(final IPage p) {
		return getPath().compareTo(p.getPath());
	}
	
	@Override
	public String toString() {
		return "Page [" + getPath() + "]";
	}
}