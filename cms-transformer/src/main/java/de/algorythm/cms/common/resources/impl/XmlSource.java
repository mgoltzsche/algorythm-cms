package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.transform.stream.StreamSource;

public class XmlSource extends StreamSource {

	public XmlSource(final String publicUri, final Path xmlFile) throws IOException {
		this(URI.create(publicUri), xmlFile);
	}

	public XmlSource(final URI publicUri, final Path xmlFile) throws IOException {
		this(publicUri, Files.newInputStream(xmlFile));
	}

	public XmlSource(final URI publicUri, final InputStream stream) throws IOException {
		super(stream, publicUri.toString());
		
		setSystemId(publicUri.getPath());
	}
}
