package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.model.entity.impl.LocaleInfo;
import de.algorythm.cms.common.model.entity.impl.LocaleInfos;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

@Singleton
public class SupportedLocalesXmlGenerator {

	private final IXmlFactory xmlFactory;

	@Inject
	public SupportedLocalesXmlGenerator(final IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	public void generateSupportedLocalesXml(final Set<Locale> supportedLocales, final IOutputTargetFactory targetFactory) throws JAXBException, IOException {
		final Marshaller marshaller = xmlFactory.createMarshaller();
		
		for (Locale locale : supportedLocales) {
			final LocaleInfos locales = createLocaleInfos(supportedLocales, locale);
			final IOutputTarget target = targetFactory.createOutputTarget('/' + locale.toLanguageTag() + "/supported-locales.xml");
			
			try (OutputStream out = target.createOutputStream()) {
				marshaller.marshal(locales, out);
			}
		}
	}

	private LocaleInfos createLocaleInfos(final Set<Locale> supportedLocales, final Locale currentLocale) {
		final LocaleInfos localeInfos = new LocaleInfos();
		final List<LocaleInfo> locales = localeInfos.getLocales();
		
		for (Locale locale : supportedLocales) {
			final String lang = locale.getLanguage();
			final String country = locale.getCountry().toLowerCase();
			final String title = locale.getDisplayLanguage(locale);
			final boolean active = currentLocale.equals(locale);
			
			locales.add(new LocaleInfo(lang, country, title, active));
		}
		
		return localeInfos;
	}

	@Override
	public String toString() {
		return "SupportedLocalesXmlGenerator";
	}
}
