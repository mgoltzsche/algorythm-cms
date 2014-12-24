package de.algorythm.cms.common.model.entity.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="locale", namespace="http://cms.algorythm.de/common/Locales")
public class LocaleInfo {

	@XmlAttribute(required = true)
	private String language;
	@XmlAttribute(required = true)
	private String title;
	@XmlAttribute(required = true)
	private String country;
	@XmlAttribute(required = true)
	private boolean active;

	public LocaleInfo() {
	}

	public LocaleInfo(final String language, final String country, final String title, final boolean active) {
		this.language = language;
		this.country = country;
		this.title = title;
		this.active = active;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((language == null) ? 0 : language.hashCode());
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
		LocaleInfo other = (LocaleInfo) obj;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}
}
