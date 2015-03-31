package de.algorythm.cms.common.impl.jaxb.adapter;

import java.net.URI;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class UriXmlAdapter extends XmlAdapter<String, URI> {

	static private final URI ROOT = URI.create("/");

	private final URI baseUri;

	public UriXmlAdapter(final URI bundleUri) {
		this.baseUri = bundleUri;
	}

	public UriXmlAdapter() {
		this.baseUri = ROOT;
	}

	@Override
	public String marshal(final URI uri) throws Exception {
		return uri == null ? null : uri.normalize().toString();
	}

	@Override
	public URI unmarshal(final String uriStr) throws Exception {
		if (uriStr == null || uriStr.isEmpty())
			throw new IllegalStateException("Undefined URI in " + baseUri);
		
		return baseUri.resolve(uriStr).normalize();
	}
}
