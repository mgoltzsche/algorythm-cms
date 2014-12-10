package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.ISchemaLocation;

@XmlRootElement(name="schema", namespace="http://cms.algorythm.de/common/Bundle")
public class SchemaLocation implements ISchemaLocation {

	@XmlAttribute(name="src")
	private URI uri;

	public SchemaLocation() {}

	public SchemaLocation(final URI uri) {
		this.uri = uri;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	public void setSchemaLocationUri(URI schemaLocationUri) {
		this.uri = schemaLocationUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((uri == null) ? 0 : uri
						.hashCode());
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
		SchemaLocation other = (SchemaLocation) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString()  {
		return uri.toString();
	}
}
