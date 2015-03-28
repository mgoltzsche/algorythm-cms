package de.algorythm.cms.common.impl.jaxb.adapter;

import java.net.URI;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class UriXmlAdapter extends XmlAdapter<String, URI> {

	private final URI bundleUri;

	public UriXmlAdapter(final URI bundleUri) {
		this.bundleUri = bundleUri;
	}

	@Override
	public String marshal(final URI uri) throws Exception {
		return uri == null ? null : uri.normalize().toString();
	}

	@Override
	public URI unmarshal(final String uriStr) throws Exception {
		if (uriStr == null || uriStr.isEmpty())
			throw new IllegalStateException("Undefined URI in " + bundleUri);
		
		return bundleUri.resolve(uriStr).normalize();
	}
}
