package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IMainResourceConfiguration;
import de.algorythm.cms.common.model.entity.IOutputConfiguration;
import de.algorythm.cms.common.model.entity.IRenderingPipelineTaskConfiguration;

@XmlRootElement(name="output", namespace="http://cms.algorythm.de/common/Site")
public class OutputConfiguration extends AbstractMergeable implements IOutputConfiguration {

	@XmlAttribute(required = true)
	private String id;
	@XmlAttribute
	private boolean enabled = true;
	@XmlElementRef(type = MainResourceConfiguration.class)
	private Set<IMainResourceConfiguration> resources = new LinkedHashSet<IMainResourceConfiguration>();
	@XmlElementRef(type = RenderingPipelineTaskConfiguration.class)
	private Set<IRenderingPipelineTaskConfiguration> tasks = new LinkedHashSet<IRenderingPipelineTaskConfiguration>();

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
	public Set<IMainResourceConfiguration> getResources() {
		return resources;
	}
	
	public void setResources(Set<IMainResourceConfiguration> resources) {
		this.resources = resources;
	}

	@Override
	public Set<IRenderingPipelineTaskConfiguration> getTasks() {
		return tasks;
	}

	public void setTasks(Set<IRenderingPipelineTaskConfiguration> tasks) {
		this.tasks = tasks;
	}

	@Override
	public IOutputConfiguration copy() {
		final OutputConfiguration r = new OutputConfiguration();
		
		r.setId(id);
		r.setEnabled(enabled);
		r.setResources(new LinkedHashSet<IMainResourceConfiguration>(resources));
		r.setTasks(new LinkedHashSet<IRenderingPipelineTaskConfiguration>(tasks));
		
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
