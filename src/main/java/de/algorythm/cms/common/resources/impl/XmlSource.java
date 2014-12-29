package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.transform.stream.StreamSource;

public class XmlSource extends StreamSource {

	public XmlSource(final String publicUri, final Path xmlFile) throws IOException {
		this(URI.create(publicUri), xmlFile);
	}
	
	public XmlSource(final URI publicUri, final Path xmlFile) throws IOException {
		super(Files.newInputStream(xmlFile), publicUri.toString());
		
		setSystemId(publicUri.getPath());
	}
}
