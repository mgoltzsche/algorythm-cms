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

import de.algorythm.cms.common.model.entity.impl.Source;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class SvgSpriteGenerator implements IRenderingJob {

	static private final Collection<URI> TEMPLATES = Collections.singleton(URI.create("/templates/de.algorythm.cms.common/SvgSprites.xsl"));
	@Inject
	private JAXBContext jaxbContext;
	private final List<URI> svg = new LinkedList<URI>();
	
	@Override
	public void run(IRenderingContext ctx) throws Exception {
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
