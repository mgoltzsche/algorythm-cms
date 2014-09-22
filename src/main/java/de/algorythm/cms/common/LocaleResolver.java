package de.algorythm.cms.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class LocaleResolver {

	private Map<String, Locale> locales = new HashMap<String, Locale>();
	
	public LocaleResolver() {
		for (Locale locale : Locale.getAvailableLocales()) {
			if (locale.getCountry().isEmpty())
				locales.put(locale.getLanguage(), locale);
			else
				locales.put(locale.getLanguage() + '_' + locale.getCountry(), locale);
			
			locales.put(locale.getISO3Language(), locale);
		}
	}
	
	public Locale getLocale(final String name) {
		final Locale locale = locales.get(name);
		
		if (locale == null)
			throw new IllegalArgumentException("Unsupported locale: " + name);
		
		return locale;
	}
}
