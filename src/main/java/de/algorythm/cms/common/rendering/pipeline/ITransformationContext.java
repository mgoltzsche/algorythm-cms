package de.algorythm.cms.common.rendering.pipeline;

import java.util.Locale;

import javax.xml.transform.Transformer;

import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;

public interface ITransformationContext {
	
	ITransformationContext createLocalized(Locale locale, boolean localizedOutput);
	Transformer createTransformer();
	IUriResolver getResourceResolver();
	IOutputUriResolver getOutputUriResolver();
}
