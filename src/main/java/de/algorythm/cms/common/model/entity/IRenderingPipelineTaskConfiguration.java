package de.algorythm.cms.common.model.entity;

public interface IRenderingPipelineTaskConfiguration {

	static public enum PipelinePhase {
		GENERATE,
		TRANSFORM,
		EXPORT;
	}
	
	Class<?> getTaskType();
	PipelinePhase getPhase();
	boolean isEnabled();
}
