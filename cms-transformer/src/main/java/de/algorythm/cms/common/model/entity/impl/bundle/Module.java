package de.algorythm.cms.common.model.entity.impl.bundle;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.algorythm.cms.common.impl.jaxb.adapter.UriXmlAdapter;
import de.algorythm.cms.common.model.entity.bundle.IModule;

@XmlRootElement(name="module", namespace="http://cms.algorythm.de/common/Bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class Module implements IModule {

	@XmlJavaTypeAdapter(value = UriXmlAdapter.class)
	@XmlSchemaType(name = "anyURI")
	@XmlElementWrapper(name = "templates", namespace = "http://cms.algorythm.de/common/Bundle")
	@XmlElements({
		@XmlElement(name = "template", namespace = "http://cms.algorythm.de/common/Bundle")
	})
	private final Set<URI> templates = new LinkedHashSet<>();
	@XmlJavaTypeAdapter(value = UriXmlAdapter.class)
	@XmlSchemaType(name = "anyURI")
	@XmlElementWrapper(name = "styles", namespace = "http://cms.algorythm.de/common/Bundle")
	@XmlElements({
		@XmlElement(name = "style", namespace = "http://cms.algorythm.de/common/Bundle")
	})
	private final Set<URI> styles = new LinkedHashSet<>();
	@XmlJavaTypeAdapter(value = UriXmlAdapter.class)
	@XmlSchemaType(name = "anyURI")
	@XmlElementWrapper(name = "scripts", namespace = "http://cms.algorythm.de/common/Bundle")
	@XmlElements({
		@XmlElement(name = "script", namespace = "http://cms.algorythm.de/common/Bundle")
	})
	private final Set<URI> scripts = new LinkedHashSet<>();
	@XmlJavaTypeAdapter(value = UriXmlAdapter.class)
	@XmlSchemaType(name = "anyURI")
	@XmlElementWrapper(name = "icons", namespace = "http://cms.algorythm.de/common/Bundle")
	@XmlElements({
		@XmlElement(name = "icon", namespace = "http://cms.algorythm.de/common/Bundle")
	})
	private final Set<URI> svgIcons = new LinkedHashSet<>();

	public Module() {}

	public Module(IModule src) {
		templates.addAll(src.getTemplates());
		styles.addAll(src.getStyles());
		scripts.addAll(src.getScripts());
		svgIcons.addAll(src.getSvgIcons());
	}

	@Override
	public Set<URI> getTemplates() {
		return templates;
	}

	@Override
	public Set<URI> getStyles() {
		return styles;
	}

	@Override
	public Set<URI> getScripts() {
		return scripts;
	}

	@Override
	public Set<URI> getSvgIcons() {
		return svgIcons;
	}
}
