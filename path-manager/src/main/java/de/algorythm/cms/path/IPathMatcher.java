package de.algorythm.cms.path;

/**
 * Instance are thread-safe and non-blocking.
 * 
 * @author Max Goltzsche <max.goltzsche@algorythm.de>
 *
 * @param <K> A path match rule's key type
 * @param <R> A path match rule's target resource type
 */
public interface IPathMatcher<K, R> {

	void match(String path, IPathMatchHandler<K, R> handler);
}
