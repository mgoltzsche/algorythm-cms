package de.algorythm.cms.common.model.entity.impl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
import de.algorythm.cms.common.model.entity.IOutputConfig;
import de.algorythm.cms.common.model.entity.IPageConfig;
import de.algorythm.cms.common.model.entity.IParam;
import de.algorythm.cms.common.model.entity.ISchemaSource;
import de.algorythm.cms.common.model.entity.ISupportedLocale;

@XmlRootElement(name="bundle", namespace="http://cms.algorythm.de/common/Bundle")
public class Bundle implements IBundle {

	@XmlAttribute(required = true)
	private String name;
	@XmlTransient
	private Path location;
	@XmlTransient
	private List<Path> rootDirectories;
	@XmlElementRef(type = SchemaSource.class)
	private final LinkedList<ISchemaSource> schemaSources = new LinkedList<ISchemaSource>();
	@XmlAttribute
	private String title;
	@XmlAttribute
	private String description;
	@XmlAttribute(name = "default-locale")
	@XmlJavaTypeAdapter(LocaleXmlAdapter.class)
	private Locale defaultLocale;
	@XmlElementRef(type = SupportedLocale.class)
	private Set<ISupportedLocale> supportedLocales = new LinkedHashSet<ISupportedLocale>();
	@XmlAttribute(name = "context-path")
	private String contextPath;
	@XmlElementRef(type = Param.class)
	private final Set<IParam> params = new LinkedHashSet<IParam>();
	@XmlElementRef(type = Dependency.class)
	private final Set<IDependency> dependencies = new LinkedHashSet<IDependency>();
	@XmlElementRef(type = OutputConfig.class)
	private final Set<IOutputConfig> output = new LinkedHashSet<IOutputConfig>();
	@XmlTransient
	private Map<String, IOutputConfig> outputMap;
	@XmlElementRef(type = PageConfig.class)
	private IPageConfig startPage;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Path getLocation() {
		return location;
	}

	public void setLocation(Path location) {
		this.location = location;
	}

	@Override
	public LinkedList<ISchemaSource> getSchemaLocations() {
		return schemaSources;
	}

	@Override
	public List<Path> getRootDirectories() {
		if (rootDirectories == null)
			throw new IllegalStateException("Bundle '" + name + "' has not been expanded");
		
		return rootDirectories;
	}

	@Override
	public void setRootDirectories(List<Path> rootDirectories) {
		this.rootDirectories = rootDirectories;
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
	public Set<ISupportedLocale> getSupportedLocales() {
		return supportedLocales;
	}

	public void setSupportedLocales(Set<ISupportedLocale> supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	@Override
	public Set<IParam> getParams() {
		return params;
	}

	@Override
	public Set<IDependency> getDependencies() {
		return dependencies;
	}

	@Override
	public Set<IOutputConfig> getOutput() {
		return output;
	}

	@Override
	public boolean containsOutput(final IOutputConfig cfg) {
		return output.contains(cfg);
	}
	
	@Override
	public boolean addOutput(final IOutputConfig cfg) {
		final boolean r = output.add(cfg);
		
		getOutputMap().put(cfg.getId(), cfg);
		
		return r;
	}
	
	@Override
	public IOutputConfig getOutput(final String id) {
		return getOutputMap().get(id);
	}

	@Override
	public IPageConfig getStartPage() {
		return startPage;
	}
	
	@Override
	public void setStartPage(IPageConfig startPage) {
		this.startPage = startPage;
	}

	private Map<String, IOutputConfig> getOutputMap() {
		if (outputMap == null) {
			outputMap = new HashMap<String, IOutputConfig>();
			
			for (IOutputConfig cfg : output)
				outputMap.put(cfg.getId(), cfg);
		}
		
		return outputMap;
	}

	@Override
	public Bundle copy() {
		final Bundle r = new Bundle();
		
		r.setLocation(location);
		r.setName(name);
		r.setTitle(title);
		r.setDescription(description);
		r.setDefaultLocale(defaultLocale);
		r.setContextPath(contextPath);
		r.setStartPage(startPage);
		r.dependencies.addAll(dependencies);
		r.params.addAll(params);
		r.supportedLocales.addAll(supportedLocales);
		
		for (ISchemaSource schemaSource : schemaSources)
			r.schemaSources.add(new SchemaSource(schemaSource.getUri().normalize()));
		
		for (IOutputConfig outputCfg : output)
			r.output.add(outputCfg.copy());
		
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
