package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.TransformerHandler;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.rendering.pipeline.impl.RenderingContext;
import de.algorythm.cms.common.resources.ISourcePathResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class BundleExpander2 {

	private final IXmlFactory xmlFactory;

	public BundleExpander2(IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	public void expandBundle(URI bundleUri) throws ResourceNotFoundException {
		final IRenderingContext renderingContext = new RenderingContext(bundle, tmpDirectory, resourcePrefix);
		final Templates templates = xmlFactory.compileTemplates(xslSourceUris, ctx);
		final Marshaller marshaller = xmlFactory.createMarshaller();
		final TransformerHandler transformerHandler = xmlFactory.createTransformerHandler(templates, ctx, outputPath, outFactory)
	}
	
	public IBundle loadBundle(final URI publicUri) {
		
	}
}
