package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * Mojo to generate an expath-pgk.xml and package it into a XAR archive.
 * 
 * @author <a href="mailto:max.goltzsche@algorythm.de">Max Goltzsche</a>
 * @version $Id$
 */
@XmlRootElement(name="xquery", namespace="http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class XQueryComponent extends AbstractComponent {

	@XmlElement(name = "namespace", namespace = "http://expath.org/ns/pkg", required = true)
	@XmlSchemaType(name = "anyURI")
	private URI namespace;

	public XQueryComponent() {}

	public XQueryComponent(URI file, URI namespace) {
		super(file);
		this.namespace = namespace;
	}

	public URI getNamespace() {
		return namespace;
	}

	public void setNamespace(URI namespace) {
		this.namespace = namespace;
	}
}
