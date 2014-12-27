package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.util.Collections;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.TransformerHandler;

import de.algorythm.cms.common.impl.TimeMeter;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class OdtTransformer implements IRenderingJob {

	@Override
	public void run(IRenderingContext ctx) throws Exception {
		final TimeMeter meter = TimeMeter.meter("ODT transformation");
		final Templates templates = ctx.compileTemplates(Collections.singleton(URI.create("/templates/odt2xhtml/export/xhtml/opendoc2xhtml.xsl")));
		final TransformerHandler handler = ctx.createTransformerHandler(templates, URI.create("/odt/index.html"));
		final Transformer transformer = handler.getTransformer();
		
		transformer.setParameter("metaFileURL", "meta.xml");
		transformer.setParameter("styleFileURL", "styles.xml");
		
		ctx.parse(URI.create("/contents/test-odt/content.xml"), handler);
		meter.finish();
	}
}
