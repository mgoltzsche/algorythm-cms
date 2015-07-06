package de.algorythm.cms.path;

public interface IPathManager<K,R> {

	MatchResult<K,R> match(String path);
}
