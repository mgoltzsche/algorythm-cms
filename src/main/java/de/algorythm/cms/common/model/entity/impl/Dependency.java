package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IDependency;

@XmlRootElement(name="dependency", namespace="http://cms.algorythm.de/common/Site")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dependency implements IDependency {

	@XmlAttribute(name = "href", required = true)
	private URI uri;
	
	@Override
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
}
