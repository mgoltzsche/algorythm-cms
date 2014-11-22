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
	IPage getStartPage();
	Set<IDependency> getDependencies();
	Set<IOutputConfiguration> getOutput();
	IOutputConfiguration getOutput(String id);
	boolean containsOutput(IOutputConfiguration cfg);
	boolean addOutput(IOutputConfiguration cfg);
	IBundle copy();
}
