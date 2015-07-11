package de.algorythm.cms.url.matcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.algorythm.cms.url.IMatchState;
import de.algorythm.cms.url.IPathMatchHandler;
import de.algorythm.cms.url.PathRule;
import de.algorythm.cms.url.PathSegmentIterator;

public class MatchState<K, R> implements IMatchState<K, R> {

	private final IPathMatchHandler<K, R> handler;
	private final PathSegmentIterator segmentIter;
	private String currentSegment;
	private PathRule<K, R> currentMatch;
	private String[] paramNames;
	private final List<String> paramValues = new LinkedList<>();

	public MatchState(final String path, final IPathMatchHandler<K, R> handler) {
		this.handler = handler;
		this.segmentIter = new PathSegmentIterator(path);

		nextSegment();
	}

	@Override
	public K getKey() {
		return currentMatch.getKey();
	}

	@Override
	public R getResource() {
		return currentMatch.getResource();
	}

	@Override
	public String getPath() {
		return segmentIter.getPath();
	}

	public String getPrefix() {
		return segmentIter.getCurrentPrefix();
	}

	public String getSuffix() {
		return segmentIter.getCurrentSuffix();
	}

	public boolean isFinalSegment() {
		return !segmentIter.hasNextSegment();
	}

	public String getCurrentSegment() {
		return currentSegment;
	}

	public boolean nextSegment() {
		if (segmentIter.hasNextSegment()) {
			currentSegment = segmentIter.nextSegment();
			return true;
		}

		return false;
	}

	public final void addParameterValue(String value) {
		paramValues.add(value);
	}

	public void matchedPrefix(final PathRule<K, R> rule, final String[] paramNames) {
		this.currentMatch = rule;
		this.paramNames = paramNames;
		handler.matchedPrefix(this);
	}

	public void matchedPositive(final PathRule<K, R> rule, final String[] paramNames) {
		this.currentMatch = rule;
		this.paramNames = paramNames;
		handler.matchedPositive(this);
	}

	public void matchedNegative() {
		this.currentMatch = null;
		handler.matchedNegative(segmentIter.getPath());
	}

	@Override
	public String[] getParamNames() {
		return Arrays.copyOf(paramNames, paramNames.length);
	}

	@Override
	public String[] getParamValues() {
		return paramValues.toArray(new String[paramValues.size()]);
	}

	@Override
	public Map<String, String> getParameters() {
		final Map<String, String> params = new HashMap<>();
		final Iterator<String> valueIter = paramValues.iterator();
		
		for (int i = 0; i < paramNames.length; i++)
			params.put(paramNames[i], valueIter.next());
		
		return params;
	}
}
