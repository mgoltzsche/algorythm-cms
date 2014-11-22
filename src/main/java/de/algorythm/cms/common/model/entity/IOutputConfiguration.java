package de.algorythm.cms.common.model.entity;

import java.util.Set;

public interface IOutputConfiguration {

	String getId();
	boolean isEnabled();
	Set<IMainResourceConfiguration> getResources();
	Set<IRenderingPipelineTaskConfiguration> getTasks();
	IOutputConfiguration copy();
}
