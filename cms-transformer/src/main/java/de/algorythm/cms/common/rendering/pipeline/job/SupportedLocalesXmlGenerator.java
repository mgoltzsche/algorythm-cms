package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
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
	
	public void generateSupportedLocalesXml(final IBundle bundle, final boolean localizeTitle, final IOutputTargetFactory targetFactory) throws JAXBException, IOException {
		final TimeMeter meter = TimeMeter.meter(bundle.getName() + ' ' + this);
		final Marshaller marshaller = xmlFactory.createMarshaller();
		
		for (ISupportedLocale supportedLocale : bundle.getSupportedLocales()) {
			final Locale locale = supportedLocale.getLocale();
			final LocaleInfos locales = createLocaleInfos(bundle, locale, localizeTitle);
			final IOutputTarget target = targetFactory.createOutputTarget('/' + locale.toLanguageTag() + "/supported-locales.xml");
			
			try (OutputStream out = target.createOutputStream()) {
				marshaller.marshal(locales, out);
			}
		}
		
		meter.finish();
	}

	private LocaleInfos createLocaleInfos(final IBundle bundle, final Locale inLocale, final boolean localizeTitle) {
		final LocaleInfos localeInfos = new LocaleInfos();
		final List<LocaleInfo> locales = localeInfos.getLocales();
		
		for (ISupportedLocale supportedLocale : bundle.getSupportedLocales()) {
			final Locale l = supportedLocale.getLocale();
			final String lang = l.getLanguage();
			final String country = l.getCountry().toLowerCase();
			final String title = localizeTitle
					? l.getDisplayLanguage(inLocale)
					: l.getDisplayLanguage(l);
			final boolean active = inLocale.equals(l);
			
			locales.add(new LocaleInfo(lang, country, title, active));
		}
		
		return localeInfos;
	}

	@Override
	public String toString() {
		return "SupportedLocalesXmlGenerator";
	}
}
