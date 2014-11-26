package de.algorythm.cms.common.rendering.pipeline;

public interface IRenderingContext {

	void addJob(Runnable job);
	String getProperty(String name);
	void setProperty(String name, String value);
}
