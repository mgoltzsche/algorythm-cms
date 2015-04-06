package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import de.algorythm.cms.common.resources.IInputResolver;

public class CmsSchemaResolver implements LSResourceResolver {

	private final IInputResolver inputResolver;

	public CmsSchemaResolver(final IInputResolver inputResolver) {
		this.inputResolver = inputResolver;
	}

	@Override
	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		final URI base = URI.create(baseURI);
		final URI publicUri = base.resolve(systemId);
		final InputStream stream;
		
		try {
			stream = inputResolver.createInputStream(publicUri);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		if (stream == null)
			throw new IllegalStateException("Cannot find XMLSchema: " + publicUri);
		
		return new XsdInput(publicUri, stream);
	}

	static private class XsdInput implements LSInput {

		private final InputStream stream;
		private final String systemId;
		private String encoding;
		private boolean certifiedText;

		public XsdInput(final URI uri, final InputStream stream) {
			this.stream = stream;
			systemId = uri.toString();
			encoding = StandardCharsets.UTF_8.name();
		}

		@Override
		public Reader getCharacterStream() {
			return null;
		}

		@Override
		public void setCharacterStream(Reader characterStream) {
			throw new UnsupportedOperationException();
		}

		@Override
		public InputStream getByteStream() {
			return stream;
		}

		@Override
		public void setByteStream(InputStream byteStream) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getStringData() {
			return null;
		}

		@Override
		public void setStringData(String stringData) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getSystemId() {
			return systemId;
		}

		@Override
		public void setSystemId(String systemId) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getPublicId() {
			return null;
		}

		@Override
		public void setPublicId(String publicId) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getBaseURI() {
			return null;
		}

		@Override
		public void setBaseURI(String baseURI) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getEncoding() {
			return encoding;
		}

		@Override
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}

		@Override
		public boolean getCertifiedText() {
			return certifiedText;
		}

		@Override
		public void setCertifiedText(boolean certifiedText) {
			this.certifiedText = certifiedText;
		}
	}
}