package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig;

@XmlRootElement(name="job", namespace="http://cms.algorythm.de/common/Bundle")
public class RenderingJobConfig extends AbstractMergeable implements IRenderingJobConfig {

	@XmlAttribute(name = "type", required = true)
	private Class<?> jobType;
	@XmlAttribute(required = true)
	private RenderingPhase phase;
	@XmlAttribute
	private boolean enabled = true;
	@XmlElementRef(type = Param.class)
	private LinkedList<IParam> params = new LinkedList<IParam>();

	@Override
	public Class<?> getJobType() {
		return jobType;
	}

	public void setJobType(Class<?> jobType) {
		this.jobType = jobType;
	}

	@Override
	public RenderingPhase getPhase() {
		return phase;
	}

	public void setPhase(RenderingPhase phase) {
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
	public LinkedList<IParam> getParams() {
		return params;
	}

	public void setParams(LinkedList<IParam> params) {
		this.params = params;
	}

	@Override
	public IRenderingJobConfig copy() {
		final RenderingJobConfig r = new RenderingJobConfig();
		
		r.setEnabled(enabled);
		r.setPhase(phase);
		r.setJobType(jobType);
		r.setParams(new LinkedList<IParam>(params));
		
		return r;
	}

	@Override
	protected String getMergeableId() {
		return (phase == null ? "" : phase.name()) + '-' + (jobType == null ? "" : jobType.getName());
	}

	@Override
	public String toString() {
		return "RenderingJobConfig [jobType=" + jobType
				+ ", phase=" + phase + "]";
	}
}
