package de.algorythm.cms.common.model.entity;

import java.util.List;

public interface IRenderingJobConfiguration {

	static public enum PipelinePhase {
		GENERATE,
		TRANSFORM,
		EXPORT;
	}
	
	Class<?> getJobType();
	PipelinePhase getPhase();
	List<IParam> getParams();
	boolean isEnabled();
	IRenderingJobConfiguration copy();
}
