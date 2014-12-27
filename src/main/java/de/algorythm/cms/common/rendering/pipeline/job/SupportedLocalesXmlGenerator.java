package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.LocaleInfo;
import de.algorythm.cms.common.model.entity.impl.LocaleInfos;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class SupportedLocalesXmlGenerator implements IRenderingJob {

	@Inject
	private JAXBContext jaxbContext;
	private boolean localizeTitle = false;

	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final TimeMeter meter = TimeMeter.meter(ctx.getBundle().getName() + ' ' + this);
		final IBundle bundle = ctx.getBundle();
		final Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		for (ISupportedLocale supportedLocale : bundle.getSupportedLocales()) {
			final Locale locale = supportedLocale.getLocale();
			final LocaleInfos locales = createLocaleInfos(bundle, locale);
			final Path outputFile = ctx.getOutputResolver().resolveUri(URI.create("tmp:///" + locale.toLanguageTag() + "/supported-locales.xml"));
			
			Files.createDirectories(outputFile.getParent());
			marshaller.marshal(locales, Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8));
		}
		
		meter.finish();
	}

	private LocaleInfos createLocaleInfos(final IBundle bundle, final Locale inLocale) {
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
