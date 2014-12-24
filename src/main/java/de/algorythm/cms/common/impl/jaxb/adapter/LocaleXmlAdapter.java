package de.algorythm.cms.common.impl.jaxb.adapter;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocaleXmlAdapter extends XmlAdapter<String, Locale> {

	@Override
	public String marshal(final Locale locale) throws Exception {
		return locale.toLanguageTag();
	}

	@Override
	public Locale unmarshal(final String localeStr) throws Exception {
		return Locale.forLanguageTag(localeStr);
	}
}
