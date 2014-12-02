package de.algorythm.cms.common.model.entity;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

public interface IBundle extends Comparable<IBundle> {

	String getName();
	URI getLocation();
	String getTitle();
	String getDescription();
	String getContextPath();
	Locale getDefaultLocale();
	String getDefaultTemplate();
	Set<IParam> getParams();
	Set<IDependency> getDependencies();
	Set<IOutputConfig> getOutput();
	IOutputConfig getOutput(String id);
	boolean containsOutput(IOutputConfig cfg);
	boolean addOutput(IOutputConfig cfg);
	IBundle copy();
}
