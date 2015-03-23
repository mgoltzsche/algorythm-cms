package de.algorythm.cms.common.model.entity.impl.bundle;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;

public abstract class AbstractSourceRef {

	@XmlAttribute(name = "src", required = true)
	@XmlSchemaType(name = "anyURI")
	private URI sourceUri;

	public URI getSourceUri() {
		return sourceUri;
	}

	public void setSourceUri(URI sourceUri) {
		this.sourceUri = sourceUri;
	}
}