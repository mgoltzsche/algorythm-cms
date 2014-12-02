package de.algorythm.cms.common.model.entity;

import java.util.Set;

public interface IOutputConfig {

	String getId();
	boolean isEnabled();
	Set<IRenderingJobConfig> getJobs();
	IOutputConfig copy();
}
