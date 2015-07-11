package de.algorythm.cms.url;

import java.util.Map;

/**
 * Instance are thread-safe and non-blocking.
 * 
 * @author Max Goltzsche <max.goltzsche@algorythm.de>
 *
 * @param <K> A URL rule's key type
 */
public interface IUrlBuilder<K> {

	/**
	 * Builds a URL with the given parameters using the URL builder registered for the given key.
	 * Some parameters will be used as path segments others as regular URL parameters depending on the URL builder. 
	 * @param params The URL parameters.
	 * @return The built URL
	 */
	String buildUrl(K key, Map<String, String> params);
	
	/**
	 * Builds a URL with the given parameters using the URL builder registered for the given key.
	 * Some parameters will be used as path segments others as regular URL parameters depending on the URL builder. 
	 * @param params The URL parameters.
	 * @return The built URL
	 */
	String buildUrl(K key, String... params);
}