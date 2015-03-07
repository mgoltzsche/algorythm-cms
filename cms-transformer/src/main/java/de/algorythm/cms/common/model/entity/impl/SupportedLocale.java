package de.algorythm.cms.common.model.entity.impl;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.ISupportedLocale;

@XmlRootElement(name="locale", namespace="http://cms.algorythm.de/common/Bundle")
public class SupportedLocale implements ISupportedLocale {

	@XmlAttribute(required = true)
	private Locale locale;

	public SupportedLocale() {}

	public SupportedLocale(final Locale locale) {
		this.locale = locale;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public int hashCode() {
		return 31 + ((locale == null) ? 0 : locale.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupportedLocale other = (SupportedLocale) obj;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return locale.toLanguageTag();
	}
}
