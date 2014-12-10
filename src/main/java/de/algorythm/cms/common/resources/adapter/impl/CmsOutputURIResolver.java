package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.OutputURIResolver;
import de.algorythm.cms.common.resources.IOutputUriResolver;

public class CmsOutputURIResolver implements OutputURIResolver {

	private final IOutputUriResolver resolver;
	
	public CmsOutputURIResolver(final IOutputUriResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public Result resolve(final String href, final String base) throws TransformerException {
		final Path publicPath = Paths.get(href);
		final Path systemBasePath = Paths.get(base);
		final Path systemPath = resolver.resolveUri(publicPath, systemBasePath);
		final Writer writer;
		
		try {
			writer = Files.newBufferedWriter(systemPath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new TransformerException("Cannot write " + systemPath, e);
		}
		
		final Result result = new StreamResult(writer);
		
		result.setSystemId(systemPath.toString());
		
		return result;
	}
	
	@Override
	public CmsOutputURIResolver newInstance() {
		return this;
	}
	
	@Override
	public void close(Result result) throws TransformerException {
	}
}
