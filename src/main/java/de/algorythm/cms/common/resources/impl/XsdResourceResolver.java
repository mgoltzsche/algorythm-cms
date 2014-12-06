package de.algorythm.cms.common.resources.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.google.common.base.Charsets;

import de.algorythm.cms.common.resources.IResourceResolver;

public class XsdResourceResolver implements LSResourceResolver {

	private final IResourceResolver uriResolver;
	
	public XsdResourceResolver(final IResourceResolver uriResolver) {
		this.uriResolver = uriResolver;
	}
	
	@Override
	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		final URI href = URI.create(systemId);
		final URI baseUri = URI.create(baseURI);
		final URI systemUri;
		
		try {
			systemUri = uriResolver.toSystemUri(href, baseUri);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		return new XsdInput(new File(systemUri));
	}
	
	static private class XsdInput implements LSInput {
		
		private final String systemId;
		private String encoding;
		private boolean certifiedText;
		
		public XsdInput(final File file) {
			systemId = file.getAbsolutePath();
			encoding = Charsets.UTF_8.name();
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
			return null;
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