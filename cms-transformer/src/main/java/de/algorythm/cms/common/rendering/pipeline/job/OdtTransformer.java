package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.TransformerHandler;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

@Singleton
public class OdtTransformer {

	private final IXmlFactory xmlFactory;

	@Inject
	public OdtTransformer(final IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	public void transformOdt(IRenderingContext ctx, IOutputTargetFactory outFactory) throws Exception {
		final TimeMeter meter = TimeMeter.meter("ODT transformation");
		//final Templates templates = ctx.compileTemplates(Collections.singleton(URI.create("/templates/odt2xhtml/export/xhtml/opendoc2xhtml.xsl")));
		final Templates templates = xmlFactory.compileTemplates(Collections.singleton(URI.create("/templates/de.algorythm.cms.common/SvgSprites.xsl")), ctx);
		final TransformerHandler handler = xmlFactory.createTransformerHandler(templates, ctx, "/odt/index.html", outFactory);
		final Transformer transformer = handler.getTransformer();
		
		transformer.setParameter("metaFileURL", "meta.xml");
		transformer.setParameter("styleFileURL", "styles.xml");
		
		xmlFactory.parse(URI.create("/contents/test-odt/content.xml"), handler, ctx);
		meter.finish();
	}
}
