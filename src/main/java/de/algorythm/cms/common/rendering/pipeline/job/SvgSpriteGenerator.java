package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;

import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.Source;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.ISourceUriResolver;

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
			
			ctx.transform(sourcesXml.getNode(), URI.create("/"), spriteUri, transformer, Locale.ROOT);
		}
	}
}
