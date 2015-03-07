package de.algorythm.cms.common.model.entity;

import java.util.Set;

public interface IContent {

	String getUuid();
	String getTitle();
	String getLanguage();
	String getType();
	Set<String> getTags();
	String getContent();
}
