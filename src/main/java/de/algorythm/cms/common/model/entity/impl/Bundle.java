package de.algorythm.cms.common.model.entity.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.algorythm.cms.common.impl.jaxb.adapter.LocaleXmlAdapter;
import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IDependency;
import de.algorythm.cms.common.model.entity.IOutputConfiguration;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.IParam;

@XmlRootElement(name="site", namespace="http://cms.algorythm.de/common/Site")
public class Bundle implements IBundle {

	@XmlAttribute(required = true)
	private String name;
	@XmlTransient
	private URI location;
	@XmlAttribute
	private String title;
	@XmlAttribute
	private String description;
	@XmlAttribute(name = "default-locale")
	@XmlJavaTypeAdapter(LocaleXmlAdapter.class)
	private Locale defaultLocale;
	@XmlAttribute(name = "default-template")
	private String defaultTemplate;
	@XmlAttribute(name = "context-path")
	private String contextPath;
	@XmlElementRef(type = Param.class)
	private Set<IParam> params = new LinkedHashSet<IParam>();
	@XmlTransient
	private IPage startPage;
	@XmlElementRef(type = Dependency.class)
	private Set<IDependency> dependencies = new LinkedHashSet<IDependency>();
	@XmlElementRef(type = OutputConfiguration.class)
	private Set<IOutputConfiguration> outputConfiguration = new LinkedHashSet<IOutputConfiguration>();
	@XmlTransient
	private Map<String, IOutputConfiguration> outputConfigurationMap;
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public URI getLocation() {
		return location;
	}

	public void setLocation(URI location) {
		this.location = location;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	
	@Override
	public String getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(String defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	@Override
	public Set<IParam> getParams() {
		return params;
	}

	public void setParams(Set<IParam> params) {
		this.params = params;
	}

	@Override
	public IPage getStartPage() {
		return startPage;
	}

	public void setStartPage(IPage startPage) {
		this.startPage = startPage;
	}

	@Override
	public Set<IDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Set<IDependency> dependencies) {
		this.dependencies = dependencies;
	}

	@Override
	public Set<IOutputConfiguration> getOutput() {
		return outputConfiguration;
	}

	public void setOutputConfiguration(
			Set<IOutputConfiguration> outputConfiguration) {
		this.outputConfiguration = outputConfiguration;
	}

	@Override
	public boolean containsOutput(final IOutputConfiguration cfg) {
		return outputConfiguration.contains(cfg);
	}
	
	@Override
	public boolean addOutput(final IOutputConfiguration cfg) {
		final boolean r = outputConfiguration.add(cfg);
		
		getOutputMap().put(cfg.getId(), cfg);
		
		return r;
	}
	
	@Override
	public IOutputConfiguration getOutput(final String id) {
		return getOutputMap().get(id);
	}

	private Map<String, IOutputConfiguration> getOutputMap() {
		if (outputConfigurationMap == null) {
			outputConfigurationMap = new HashMap<String, IOutputConfiguration>();
			
			for (IOutputConfiguration cfg : outputConfiguration)
				outputConfigurationMap.put(cfg.getId(), cfg);
		}
		
		return outputConfigurationMap;
	}

	@Override
	public Bundle copy() {
		final Bundle r = new Bundle();
		
		r.setName(name);
		r.setLocation(location);
		r.setTitle(title);
		r.setDescription(description);
		r.setDefaultLocale(defaultLocale);
		r.setDefaultTemplate(defaultTemplate);
		r.setContextPath(contextPath);
		r.setStartPage(startPage);
		r.setDependencies(new LinkedHashSet<IDependency>(dependencies));
		
		for (IOutputConfiguration output : outputConfiguration)
			r.getOutput().add(output.copy());
		
		return r;
	}
	
	@Override
	public int compareTo(final IBundle bundle) {
		return name.compareTo(bundle.getName());
	}

	@Override
	public String toString() {
		return "Site [" + getName() + "]";
	}
}
