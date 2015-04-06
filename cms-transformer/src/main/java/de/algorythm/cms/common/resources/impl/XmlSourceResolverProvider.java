package de.algorythm.cms.common.resources.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.algorythm.cms.common.rendering.pipeline.IXmlSourceResolverProvider;
import de.algorythm.cms.common.resources.IXmlSourceResolver;

@Singleton
public class XmlSourceResolverProvider implements IXmlSourceResolverProvider {

	private final IXmlSourceResolver xmlSourceResolver;

	@Inject
	public XmlSourceResolverProvider(final DefaultXmlSourceResolver xmlResolver, final OdtXmlSourceResolver odtResolver) {
		final Map<String, IXmlSourceResolver> extensionMap = new HashMap<String, IXmlSourceResolver>();
		
		extensionMap.put("xml", xmlResolver);
		extensionMap.put("xsl", xmlResolver);
		extensionMap.put("xsd", xmlResolver);
		extensionMap.put("svg", xmlResolver);
		extensionMap.put("odt", odtResolver);
		
		xmlSourceResolver = new XmlSourceResolverDelegator(extensionMap);
	}
	
	@Override
	public IXmlSourceResolver getXmlSourceResolver() {
		return xmlSourceResolver;
	}
}
