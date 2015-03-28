package de.algorythm.cms.common.model.entity.impl.bundle;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.algorythm.cms.common.impl.jaxb.adapter.UriXmlAdapter;
import de.algorythm.cms.common.model.entity.bundle.ITheme;

@XmlRootElement(name="theme", namespace="http://cms.algorythm.de/common/Bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class Theme extends Module implements ITheme {

	@XmlJavaTypeAdapter(value = UriXmlAdapter.class)
	@XmlAttribute(name = "base")
	@XmlSchemaType(name = "anyURI")
	private URI baseTheme;

	public Theme() {}
	
	public Theme(ITheme src) {
		baseTheme = src.getBaseTheme();
	}

	@Override
	public URI getBaseTheme() {
		return baseTheme;
	}

	public void setBaseTheme(URI baseTheme) {
		this.baseTheme = baseTheme;
	}
}
