package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IMainResourceConfiguration;

@XmlRootElement(name="resource", namespace="http://cms.algorythm.de/common/Site")
public class MainResourceConfiguration extends AbstractMergeable implements IMainResourceConfiguration {

	@XmlAttribute(required = true)
	private URI uri;
	@XmlAttribute
	private boolean enabled = true;

	@Override
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getMergeableId() {
		return uri.toString();
	}

	@Override
	public String toString() {
		return "MainResourceConfiguration [uri=" + uri + "]";
	}
}
