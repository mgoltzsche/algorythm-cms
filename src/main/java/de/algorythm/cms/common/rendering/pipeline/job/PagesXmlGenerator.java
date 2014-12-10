package de.algorythm.cms.common.rendering.pipeline.job;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class PagesXmlGenerator implements IRenderingJob {

	@Inject
	private IBundleLoader pageLoader;
	@Inject
	private JAXBContext jaxbContext;
	
	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final IBundle bundle = ctx.getBundle();
		final Path outputDir = ctx.getTempDirectory();
		
		for (ISupportedLocale supportedLocale : bundle.getSupportedLocales()) {
			final Locale locale = supportedLocale.getLocale();
			final Path localizedDir = outputDir.resolve(locale.getLanguage());
			
			Files.createDirectories(localizedDir);
			ctx.execute(new IRenderingJob() {
				@Override
				public void run(final IRenderingContext ctx) throws Exception {
					final IPage startPage = pageLoader.loadPages(bundle, locale);
					final Path pagesXml = localizedDir.resolve("pages.xml");
					final Marshaller marshaller = jaxbContext.createMarshaller();
					
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					marshaller.marshal(startPage, Files.newBufferedWriter(pagesXml, StandardCharsets.UTF_8));
				}
				@Override
				public String toString() {
					return PagesXmlGenerator.class.getSimpleName() + ":" + locale.getLanguage();
				}
			});
		}
	}

	@Override
	public int hashCode() {
		return 31 * 1 + getClass().getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() == obj.getClass())
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
