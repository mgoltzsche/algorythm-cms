package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * expath package xsd component description.
 * @see <a href="http://expath.org/spec/pkg#expath-pkg.xsd">http://expath.org/spec/pkg#expath-pkg.xsd</a>
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
@XmlRootElement(name="xsd", namespace="http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class XsdComponent extends Component {

	public XsdComponent() {}

	public XsdComponent(URI file, URI importUri) {
		super(file, importUri);
	}

	@Override
	public String getType() {
		return XSD;
	}
}
