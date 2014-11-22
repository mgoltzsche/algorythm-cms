package de.algorythm.cms.common.model.entity.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IRenderingPipelineTaskConfiguration;

@XmlRootElement(name="task", namespace="http://cms.algorythm.de/common/Site")
public class RenderingPipelineTaskConfiguration extends AbstractMergeable implements IRenderingPipelineTaskConfiguration {

	@XmlAttribute(name = "type", required = true)
	private Class<?> taskType;
	@XmlAttribute(required = true)
	private PipelinePhase phase;
	@XmlAttribute
	private boolean enabled = true;

	@Override
	public Class<?> getTaskType() {
		return taskType;
	}

	public void setTaskType(Class<?> taskType) {
		this.taskType = taskType;
	}

	@Override
	public PipelinePhase getPhase() {
		return phase;
	}

	public void setPhase(PipelinePhase phase) {
		this.phase = phase;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	protected String getMergeableId() {
		return phase.name() + '-' + taskType.getName();
	}

	@Override
	public String toString() {
		return "RenderingPipelineTaskConfiguration [taskType=" + taskType
				+ ", phase=" + phase + "]";
	}
}
