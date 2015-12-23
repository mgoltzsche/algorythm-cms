package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;

import java.net.URI;

/**
 * expath package xslt component description.
 * @see <a href="http://expath.org/spec/pkg#expath-pkg.xsd">http://expath.org/spec/pkg#expath-pkg.xsd</a>
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
@XmlRootElement(name="xslt", namespace="http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class XsltComponent extends Component {

	public XsltComponent() {}

	public XsltComponent(URI file, URI importUri) {
		super(file, importUri);
	}

	@Override
	public String getType() {
		return XSLT;
	}
}
