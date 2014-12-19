package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import de.algorythm.cms.common.model.entity.ISource;

@XmlRootElement(name="source", namespace="http://cms.algorythm.de/common/Sources")
public class Source implements ISource {

	@XmlAttribute(required = true)
	@XmlSchemaType(name = "anyURI")
	private URI uri;

	public Source() {}

	public Source(final URI uri) {
		this.uri = uri;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
}
