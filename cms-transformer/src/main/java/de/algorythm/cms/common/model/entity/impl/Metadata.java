package de.algorythm.cms.common.model.entity.impl;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IMetadata;

@XmlRootElement(name="metadata", namespace="http://cms.algorythm.de/common/Metadata")
public class Metadata implements IMetadata {

	@XmlAttribute(required = true)
	private String title;
	@XmlAttribute(name = "short-title")
	private String shortTitle;
	@XmlAttribute(name = "created")
	private Date creationTime;
	@XmlAttribute(name = "modified")
	private Date lastModifiedTime;

	public Metadata() {}

	public Metadata(Date creationTime, Date lastModifiedTime) {
		this.creationTime = creationTime;
		this.lastModifiedTime = lastModifiedTime;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	@Override
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
}
