package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.xml.transform.Source;

import org.apache.commons.io.FilenameUtils;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;

public class XmlSourceResolverDelegator implements IXmlSourceResolver {

	private final Map<String, IXmlSourceResolver> extensionMap;
	
	public XmlSourceResolverDelegator(final Map<String, IXmlSourceResolver> extensionMap) {
		this.extensionMap = extensionMap;
	}
	
	@Override
	public Source createXmlSource(URI uri, IRenderingContext ctx)
			throws ResourceNotFoundException, IOException {
		final String path = uri.getPath();
		
		if (path == null)
			throw new IllegalStateException("URI " + uri + " does not define path");
		
		final String extension = FilenameUtils.getExtension(path).toLowerCase();
		
		if (extension.isEmpty())
			throw new IllegalArgumentException(uri + " does not define a file handler");
		
		final IXmlSourceResolver resolver = extensionMap.get(extension);
		
		if (resolver == null)
			throw new UnsupportedOperationException("No IXmlSourceResolver available to convert from *." + extension + " file to XML");
		
		return resolver.createXmlSource(uri, ctx);
	}
}
