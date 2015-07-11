package de.algorythm.cms.url;

/**
 * Instance are thread-safe and non-blocking.
 * 
 * @author Max Goltzsche <max.goltzsche@algorythm.de>
 *
 * @param <K> A match rule's key type
 * @param <R> A match rule's target resource type
 */
public interface IUrlManager<K, R> extends IPathMatcher<K, R>, IUrlBuilder<K> {
}