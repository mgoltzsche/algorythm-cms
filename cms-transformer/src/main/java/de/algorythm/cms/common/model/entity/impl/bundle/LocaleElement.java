package de.algorythm.cms.common.model.entity.impl.bundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="locale", namespace="http://cms.algorythm.de/common/Bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class LocaleElement {

	@XmlAttribute(name = "tag", required = true)
	private String languageTag;

	public LocaleElement() {}

	public LocaleElement(String languageTag) {
		this.languageTag = languageTag;
	}

	public String getLanguageTag() {
		return languageTag;
	}

	public void setLanguageTag(String languageTag) {
		this.languageTag = languageTag;
	}
}
