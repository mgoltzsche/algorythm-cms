package de.algorythm.cms.common.model.entity.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IMetadata;

@XmlRootElement(name="metadata", namespace="http://cms.algorythm.de/common/Metadata")
public class Metadata implements IMetadata {

	@XmlAttribute(required = true)
	private String title;
	@XmlAttribute
	private String shortTitle;
	@XmlAttribute
	private Date lastModified;

	public Metadata() {}

	public Metadata(Path file) throws IOException {
		title = file.getFileName().toString();
		lastModified = new Date(Files.getLastModifiedTime(file).toMillis());
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
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
}
