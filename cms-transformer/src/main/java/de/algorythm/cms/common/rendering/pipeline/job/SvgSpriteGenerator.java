package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.TransformerHandler;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.model.entity.impl.Sources;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IInputSource;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

@Singleton
public class SvgSpriteGenerator {

	static public final URI FLAG_DIR_URI = URI.create("/de/algorythm/cms/common/icons/flags/");
	static private final URI XSL_URI = URI.create("/de/algorythm/cms/common/transformations/SvgSprites.xsl");

	private final IXmlFactory xmlFactory;

	@Inject
	public SvgSpriteGenerator(IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	public void generateSvgSprite(final IRenderingContext ctx, final Collection<URI> svgs, final Set<Locale> supportedLocales, final IOutputTargetFactory outFactory) throws Exception {
		final TimeMeter meter = TimeMeter.meter(this.toString());
		
		if (!supportedLocales.isEmpty()) {
			for (Locale locale : supportedLocales) {
				final String country = locale.getCountry().toLowerCase();
				final URI flagUri = FLAG_DIR_URI.resolve(country + ".svg");
				final IInputSource flag = ctx.resolveResource(flagUri);
				
				if (country.isEmpty())
					throw new IllegalStateException("Undefined country in locale: " + locale);
				
				if (flag == null)
					throw new IllegalStateException("Missing file: " + flagUri);
				
				svgs.add(flagUri);
			}
		}
		
		if (!svgs.isEmpty()) {
			final String outputPath = ctx.getResourcePrefix() + "sprites.svg";
			final Templates templates = xmlFactory.compileTemplates(XSL_URI, ctx);
			final TransformerHandler handler = xmlFactory.createTransformerHandler(templates, ctx, outputPath, outFactory);
			
			xmlFactory.createMarshaller().marshal(new Sources(svgs), handler);
		}
		
		meter.finish();
	}

	@Override
	public String toString() {
		return "SvgSpriteGenerator";
	}
}
