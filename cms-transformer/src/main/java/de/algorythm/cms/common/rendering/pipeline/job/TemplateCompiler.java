package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import de.algorythm.cms.common.model.entity.bundle.IOutputConfig;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;

public class TemplateCompiler {

	private final IXmlFactory xmlFactory;

	@Inject
	public TemplateCompiler(IXmlFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	public Templates compileTemplates(final IOutputConfig sources, final IRenderingContext ctx) throws TransformerConfigurationException {
		final Set<URI> sourceSet = new LinkedHashSet<>();
		
		sourceSet.addAll(sources.getTheme().getTemplates());
		sourceSet.addAll(sources.getModule().getTemplates());
		
		return xmlFactory.compileTemplates(sourceSet, ctx);
	}
}
