package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.rendering.pipeline.impl.TemplateErrorListener;
import de.algorythm.cms.common.resources.ISourceUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsTemplateURIResolver;

public class SvgSpriteGenerator implements IRenderingJob {

	static private final URI XSL_URI = URI.create("/templates/de.algorythm.cms.common/SvgSprites.xsl");
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
			final URI outputUri = URI.create(".." + ctx.getResourcePrefix() + "/sprites.svg");
			final Path outputPath = ctx.getOutputResolver().resolveUri(outputUri, Locale.ROOT);
			Files.createDirectories(outputPath.getParent());
			final OutputStream outputStream = Files.newOutputStream(outputPath);
			final ISourceUriResolver resolver = ctx.getResourceResolver();
			final Path xslPath = resolver.resolve(XSL_URI, Locale.ROOT);
			final InputStream xslStream = Files.newInputStream(xslPath);
			final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			final TemplateErrorListener errorListener = new TemplateErrorListener();
			transformerFactory.setErrorListener(errorListener);
			transformerFactory.setURIResolver(new CmsTemplateURIResolver(resolver));
			final Templates templates = transformerFactory.newTemplates(new StreamSource(xslStream, XSL_URI.toString()));
			final TransformerHandler handler = transformerFactory.newTransformerHandler(templates);
			final StreamResult result = new StreamResult(outputStream);
			result.setSystemId(outputUri.toString());
			handler.setSystemId("/sprites.svg");
			handler.setResult(result);
			
			jaxbContext.createMarshaller().marshal(new Sources(svg), handler);
		}
		
		meter.finish();
	}

	@Override
	public String toString() {
		return "SvgSpriteGenerator";
	}
}
