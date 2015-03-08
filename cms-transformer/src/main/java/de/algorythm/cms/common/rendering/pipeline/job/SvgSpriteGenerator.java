package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.TransformerHandler;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class SvgSpriteGenerator implements IRenderingJob {

	static private final URI XSL_URI = URI.create("/transformations/de.algorythm.cms.common/SvgSprites.xsl");
	static private final URI FLAG_DIRECTORY = URI.create("/images/flags/");

	@Inject
	private JAXBContext jaxbContext;
	private final List<URI> svg = new LinkedList<URI>();
	private boolean includeLocaleFlags = true;
	private URI flagDirectory = FLAG_DIRECTORY;
	
	
	@Override
	public void run(IRenderingContext ctx) throws Exception {
		final TimeMeter meter = TimeMeter.meter(ctx.getBundle().getName() + ' ' + this);
		
		if (includeLocaleFlags) {
			for (ISupportedLocale supportedLocale : ctx.getBundle().getSupportedLocales()) {
				final Locale locale = supportedLocale.getLocale();
				final String country = locale.getCountry().toLowerCase();
				
				if (country.isEmpty())
					throw new IllegalStateException("Undefined country in locale " + locale);
				
				svg.add(flagDirectory.resolve(country + ".svg"));
			}
		}
		
		if (!svg.isEmpty()) {
			final String outputPath = ctx.getResourcePrefix() + "/sprites.svg";
			final Templates templates = ctx.compileTemplates(XSL_URI);
			final TransformerHandler handler = ctx.createTransformerHandler(templates, outputPath, ctx);
			
			jaxbContext.createMarshaller().marshal(new Sources(svg), handler);
		}
		
		meter.finish();
	}

	@Override
	public String toString() {
		return "SvgSpriteGenerator";
	}
}
