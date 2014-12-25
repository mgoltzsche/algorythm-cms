package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.Source;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class SvgSpriteGenerator implements IRenderingJob {

	static private final Collection<URI> TEMPLATES = Collections.singleton(URI.create("/templates/de.algorythm.cms.common/SvgSprites.xsl"));
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
			final URI spriteUri = URI.create("/.." + ctx.getResourcePrefix() + "/sprites.svg");
			final Templates templates = ctx.compileTemplates(TEMPLATES);
			final Transformer transformer = ctx.createTransformer(templates, null, Locale.ROOT);
			final Marshaller marshaller = jaxbContext.createMarshaller();
			final DOMResult sourcesXml = new DOMResult();
			final Sources sources = new Sources();
			
			for (URI uri : svg)
				sources.getSources().add(new Source(uri));
			
			marshaller.marshal(sources, sourcesXml);
			
			final javax.xml.transform.Source xsltSource = new DOMSource(sourcesXml.getNode(), "/sprites.svg");
			
			ctx.transform(xsltSource, spriteUri, transformer, Locale.ROOT);
		}
		
		meter.finish();
	}

	@Override
	public String toString() {
		return "SvgSpriteGenerator";
	}
}
