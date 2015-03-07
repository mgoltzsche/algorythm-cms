package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="page", namespace="http://cms.algorythm.de/common/Page")
public class PageFeed {

	@XmlAttribute(required = true)
	private String name;
	@XmlAttribute(required = true)
	private String path;
	@XmlAttribute(required = true)
	private URI content;

	public PageFeed() {}
	
	public PageFeed(String name, String path, URI contentUri) {
		this.name = name;
		this.path = path;
		this.content = contentUri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public URI getContent() {
		return content;
	}

	public void setContent(URI content) {
		this.content = content;
	}
}
