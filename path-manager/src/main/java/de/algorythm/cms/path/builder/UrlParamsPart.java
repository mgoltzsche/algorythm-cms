package de.algorythm.cms.path.builder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class UrlParamsPart extends AUrlPart {

	static public final UrlParamsPart INSTANCE = new UrlParamsPart();
	
	private UrlParamsPart() {}
	
	@Override
	public void buildUrl(Map<String, String> params, StringBuilder url) {
		if (!params.isEmpty()) {
			url.append('?');
			
			final Iterator<Entry<String, String>> iter = params.entrySet().iterator();
			
			appendParam(iter.next(), url);
			
			while (iter.hasNext()) {
				url.append('&');
				appendParam(iter.next(), url);
			}
		}
	}
	
	private void appendParam(Entry<String, String> param, StringBuilder url) {
		url.append(encode(param.getKey())).append('=').append(encode(param.getValue()));
	}
	
	private String encode(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		} catch(UnsupportedEncodingException e) {
			throw new IllegalStateException("Cannot encode URL param", e);
		}
	}

	@Override
	public String toString() {
		return "?";
	}
}
