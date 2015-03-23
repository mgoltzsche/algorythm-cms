package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.TransformerHandler;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

@Singleton
public class SvgSpriteGenerator {

	static public final URI FLAG_DIRECTORY = URI.create("/images/flags/");
	static private final URI XSL_URI = URI.create("/transformations/de.algorythm.cms.common/SvgSprites.xsl");

	private final IXmlFactory xmlFactory;

	@Inject
	public SvgSpriteGenerator(IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	public void generateSvgSprite(final IRenderingContext ctx, final List<URI> svg, final URI flagDirectory, final boolean includeFlags, final IOutputTargetFactory targetFactory) throws Exception {
		final TimeMeter meter = TimeMeter.meter(ctx.getBundle().getName() + ' ' + this);
		
		if (includeFlags) {
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
			final Templates templates = xmlFactory.compileTemplates(XSL_URI, ctx);
			final TransformerHandler handler = xmlFactory.createTransformerHandler(templates, ctx, outputPath, targetFactory);
			
			xmlFactory.createMarshaller().marshal(new Sources(svg), handler);
		}
		
		meter.finish();
	}

	@Override
	public String toString() {
		return "SvgSpriteGenerator";
	}
}
