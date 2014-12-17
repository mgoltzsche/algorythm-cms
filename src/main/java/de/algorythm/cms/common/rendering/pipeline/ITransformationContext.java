package de.algorythm.cms.common.rendering.pipeline;

import java.util.Locale;

import javax.xml.transform.Transformer;

import de.algorythm.cms.common.resources.ITargetUriResolver;
import de.algorythm.cms.common.resources.ISourceUriResolver;

public interface ITransformationContext {
	
	ITransformationContext createLocalized(Locale locale, boolean localizedOutput);
	Transformer createTransformer();
	ISourceUriResolver getResourceResolver();
	ITargetUriResolver getOutputUriResolver();
}
