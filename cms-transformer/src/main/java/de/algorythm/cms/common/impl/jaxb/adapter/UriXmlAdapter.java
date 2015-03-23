package de.algorythm.cms.common.impl.jaxb.adapter;

import java.net.URI;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class UriXmlAdapter extends XmlAdapter<String, URI> {

	@Override
	public String marshal(final URI uri) throws Exception {
		return uri == null ? null : uri.normalize().toString();
	}

	@Override
	public URI unmarshal(final String uriStr) throws Exception {
		return uriStr == null ? null : new URI(uriStr).normalize();
	}
}
