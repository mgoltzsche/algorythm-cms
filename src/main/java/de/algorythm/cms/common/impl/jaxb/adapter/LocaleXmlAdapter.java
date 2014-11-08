package de.algorythm.cms.common.impl.jaxb.adapter;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.algorythm.cms.common.LocaleResolver;

public class LocaleXmlAdapter extends XmlAdapter<String, Locale> {

	private final LocaleResolver localeResolver = new LocaleResolver();
	
	@Override
	public String marshal(final Locale locale) throws Exception {
		return locale.getISO3Language();
	}

	@Override
	public Locale unmarshal(final String localeStr) throws Exception {
		return localeResolver.getLocale(localeStr);
	}
}
