package de.algorythm.cms.common.model.entity;

import java.util.LinkedList;

public interface IRenderingJobConfig {

	static public enum RenderingPhase {
		GENERATE,
		TRANSFORM,
		EXPORT;
	}
	
	Class<?> getJobType();
	RenderingPhase getPhase();
	LinkedList<IParam> getParams();
	boolean isEnabled();
	IRenderingJobConfig copy();
}
