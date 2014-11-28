package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IOutputConfiguration;
import de.algorythm.cms.common.model.entity.IRenderingJobConfiguration;

@XmlRootElement(name="output", namespace="http://cms.algorythm.de/common/Site")
public class OutputConfiguration extends AbstractMergeable implements IOutputConfiguration {

	@XmlAttribute(required = true)
	private String id;
	@XmlAttribute
	private boolean enabled = true;
	@XmlElementRef(type = RenderingJobConfiguration.class)
	private Set<IRenderingJobConfiguration> jobs = new LinkedHashSet<IRenderingJobConfiguration>();

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
	public Set<IRenderingJobConfiguration> getJobs() {
		return jobs;
	}

	public void setJobs(Set<IRenderingJobConfiguration> jobs) {
		this.jobs = jobs;
	}

	@Override
	public IOutputConfiguration copy() {
		final OutputConfiguration r = new OutputConfiguration();
		final Set<IRenderingJobConfiguration> jobs = new LinkedHashSet<IRenderingJobConfiguration>();
		
		for (IRenderingJobConfiguration job : this.jobs)
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
		return "OutputConfiguration [id=" + id + "]";
	}
}
