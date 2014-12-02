package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IOutputConfig;
import de.algorythm.cms.common.model.entity.IRenderingJobConfig;

@XmlRootElement(name="output", namespace="http://cms.algorythm.de/common/Bundle")
public class OutputConfig extends AbstractMergeable implements IOutputConfig {

	@XmlAttribute(required = true)
	private String id;
	@XmlAttribute
	private boolean enabled = true;
	@XmlElementRef(type = RenderingJobConfig.class)
	private Set<IRenderingJobConfig> jobs = new LinkedHashSet<IRenderingJobConfig>();

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Set<IRenderingJobConfig> getJobs() {
		return jobs;
	}

	public void setJobs(Set<IRenderingJobConfig> jobs) {
		this.jobs = jobs;
	}

	@Override
	public IOutputConfig copy() {
		final OutputConfig r = new OutputConfig();
		final Set<IRenderingJobConfig> jobs = new LinkedHashSet<IRenderingJobConfig>();
		
		for (IRenderingJobConfig job : this.jobs)
			jobs.add(job.copy());
		
		r.setId(id);
		r.setEnabled(enabled);
		r.setJobs(jobs);
		
		return r;
	}

	@Override
	protected String getMergeableId() {
		return id;
	}

	@Override
	public String toString() {
		return "OutputConfig [id=" + id + "]";
	}
}
