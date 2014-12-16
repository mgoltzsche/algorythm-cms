package de.algorythm.cms.common.resources.adapter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import de.algorythm.cms.common.resources.IUriResolver;

public class CmsSchemaResolver implements LSResourceResolver {

	private final IUriResolver uriResolver;
	
	public CmsSchemaResolver(final IUriResolver uriResolver) {
		this.uriResolver = uriResolver;
	}
	
	@Override
	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		final URI base = URI.create(baseURI);
		final URI publicUri = base.resolve(systemId);
		final Path systemPath = uriResolver.resolve(publicUri);
		
		return new XsdInput(publicUri, systemPath);
	}
	
	static private class XsdInput implements LSInput {
		
		private final Path path;
		private final String systemId;
		private String encoding;
		private boolean certifiedText;
		
		public XsdInput(final URI uri, final Path path) {
			this.path = path;
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
			try {
				return Files.newInputStream(path);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read " + path, e);
			}
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