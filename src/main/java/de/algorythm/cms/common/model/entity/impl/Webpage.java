package de.algorythm.cms.common.model.entity.impl;

import de.algorythm.cms.common.model.entity.IContent;

public class Webpage {

	private String uuid;
	private String name;
	private String title;
	private Website website;
	private Webpage parent;
	private IContent content;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Website getWebsite() {
		return website;
	}

	public void setWebsite(Website website) {
		this.website = website;
	}

	public Webpage getParent() {
		return parent;
	}

	public void setParent(Webpage parent) {
		this.parent = parent;
	}

	public IContent getContent() {
		return content;
	}

	public void setContent(IContent content) {
		this.content = content;
	}
}