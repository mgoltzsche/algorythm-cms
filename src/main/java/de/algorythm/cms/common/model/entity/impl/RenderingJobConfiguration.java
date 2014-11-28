package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfiguration;

@XmlRootElement(name="job", namespace="http://cms.algorythm.de/common/Site")
public class RenderingJobConfiguration extends AbstractMergeable implements IRenderingJobConfiguration {

	@XmlAttribute(name = "type", required = true)
	private Class<?> jobType;
	@XmlAttribute(required = true)
	private PipelinePhase phase;
	@XmlAttribute
	private boolean enabled = true;
	@XmlElementRef(type = Param.class)
	private List<IParam> params = new LinkedList<IParam>();

	@Override
	public Class<?> getJobType() {
		return jobType;
	}

	public void setJobType(Class<?> jobType) {
		this.jobType = jobType;
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
	public List<IParam> getParams() {
		return params;
	}

	public void setParams(List<IParam> params) {
		this.params = params;
	}

	@Override
	public IRenderingJobConfiguration copy() {
		final RenderingJobConfiguration r = new RenderingJobConfiguration();
		
		r.setEnabled(enabled);
		r.setPhase(phase);
		r.setJobType(jobType);
		r.setParams(new LinkedList<IParam>(params));
		
		return r;
	}

	@Override
	protected String getMergeableId() {
		return phase.name() + '-' + jobType.getName();
	}

	@Override
	public String toString() {
		return "RenderingPipelineTaskConfiguration [taskType=" + jobType
				+ ", phase=" + phase + "]";
	}
}
