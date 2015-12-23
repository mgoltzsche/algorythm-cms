package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * expath package xquery component description.
 * @see <a href="http://expath.org/spec/pkg#expath-pkg.xsd">http://expath.org/spec/pkg#expath-pkg.xsd</a>
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
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

	@Override
	public String getType() {
		return XQUERY;
	}

	@Override
	public String getName() {
		return namespace.toASCIIString();
	}

	public URI getNamespace() {
		return namespace;
	}

	public void setNamespace(URI namespace) {
		this.namespace = namespace;
	}
}
