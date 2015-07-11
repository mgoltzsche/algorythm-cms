package de.algorythm.cms.url.builder;

import static de.algorythm.cms.url.RuleSegment.EMPTY_SEGMENT;
import static de.algorythm.cms.url.RuleSegment.PATH_PARAM;
import static de.algorythm.cms.url.RuleSegment.SEGMENT;
import static de.algorythm.cms.url.RuleSegment.SEGMENT_PARAM;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.algorythm.cms.url.IUrlBuilder;
import de.algorythm.cms.url.PathRule;
import de.algorythm.cms.url.PathRuleException;
import de.algorythm.cms.url.PathSegmentIterator;
import de.algorythm.cms.url.RuleSegment;

public class UrlBuilderBuilder<K> {

	private final String urlPrefix;
	private final List<PathRule<K, ?>> rules = new LinkedList<>();
	
	public UrlBuilderBuilder(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	
	public UrlBuilderBuilder<K> addRule(PathRule<K, ?> rule) {
		rules.add(rule);
		return this;
	}
	
	public IUrlBuilder<K> build() throws PathRuleException {
		final Map<K, AUrlPart> urlMap = new HashMap<>();
		
		for (PathRule<K, ?> rule : rules) {
			if (urlMap.containsKey(rule.getKey()))
				throw new PathRuleException("Duplicate rule key " + rule.getKey());
			
			AUrlPart urlBuildRule = compilePath(rule.getPattern());
			
			urlMap.put(rule.getKey(), urlBuildRule);
		}
		
		return new UrlBuilder<K>(urlMap);
	}
	
	private AUrlPart compilePath(String rule) {
		final AUrlPart result = new UrlPart("");
		AUrlPart currentPart = result;
		final PathSegmentIterator segmentIter = new PathSegmentIterator(rule);
		StringBuilder prefixBuilder = new StringBuilder(urlPrefix);
		String prefix = null;
		boolean first = true;
		
		while (segmentIter.hasNextSegment()) {
			final String segment = segmentIter.nextSegment();
			final RuleSegment ruleSegment = RuleSegment.forSegment(segment);
			
			switch(ruleSegment.getType()) {
			case SEGMENT:
				prefixBuilder.append('/').append(ruleSegment.getKey());
				break;
			case EMPTY_SEGMENT:
				if (!first)
					prefixBuilder.append('/');
				break;
			case SEGMENT_PARAM:
				prefix = prefixBuilder.append('/').toString();
				currentPart.setSuffix(new UrlPart(prefix));
				currentPart = currentPart.getSuffix();
				currentPart.setSuffix(new PathSegmentParamPart(ruleSegment.getKey()));
				currentPart = currentPart.getSuffix();
				prefixBuilder = new StringBuilder();
				break;
			case PATH_PARAM:
				prefix = prefixBuilder.append('/').toString();
				currentPart.setSuffix(new UrlPart(prefix));
				currentPart = currentPart.getSuffix();
				currentPart.setSuffix(new PathParamPart(ruleSegment.getKey()));
				currentPart = currentPart.getSuffix();
				prefixBuilder = new StringBuilder();
				break;
			default:
				throw new IllegalStateException("Unsupported rule segment type");
			}
			
			first = false;
		}
		
		if (prefixBuilder.length() > 0) {
			prefix = prefixBuilder.toString();
			currentPart.setSuffix(new UrlPart(prefix));
			currentPart = currentPart.getSuffix();
		}
		
		return result.getSuffix();
	}
}
