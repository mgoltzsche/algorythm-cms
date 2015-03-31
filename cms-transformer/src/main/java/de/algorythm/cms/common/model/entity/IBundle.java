package de.algorythm.cms.common.model.entity;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.algorythm.cms.common.model.entity.bundle.IPage;

public interface IBundle extends Comparable<IBundle> {

	String getName();
	Path getLocation();
	LinkedList<ISchemaSource> getSchemaLocations();
	List<Path> getRootDirectories();
	void setRootDirectories(List<Path> rootDirectories);
	String getTitle();
	String getDescription();
	String getContextPath();
	Locale getDefaultLocale();
	Set<ISupportedLocale> getSupportedLocales();
	Set<IParam> getParams();
	Set<IDependency> getDependencies();
	Set<IOutputConfig> getOutput();
	IOutputConfig getOutput(String id);
	boolean containsOutput(IOutputConfig cfg);
	boolean addOutput(IOutputConfig cfg);
	IPage getStartPage();
	void setStartPage(IPage page);
	IBundle copy();
}
