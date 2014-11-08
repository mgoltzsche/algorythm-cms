package de.algorythm.cms.common.model.entity.impl;

import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.algorythm.cms.common.impl.jaxb.adapter.LocaleXmlAdapter;
import de.algorythm.cms.common.model.entity.IDependency;
import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.entity.ISite;

@XmlRootElement(name="site", namespace="http://cms.algorythm.de/common/Site")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteInfo implements ISite {

	@XmlTransient
	private String name;
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
	@XmlTransient
	private IPage startPage;
	@XmlElementRef(type = Dependency.class)
	private List<IDependency> dependencies;
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	public IPage getStartPage() {
		return startPage;
	}

	public void setStartPage(IPage startPage) {
		this.startPage = startPage;
	}

	@Override
	public List<IDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<IDependency> dependencies) {
		this.dependencies = dependencies;
	}

	@Override
	public int compareTo(final ISite site) {
		return name.compareTo(site.getName());
	}

	@Override
	public String toString() {
		return "Site [" + getName() + "]";
	}
}
