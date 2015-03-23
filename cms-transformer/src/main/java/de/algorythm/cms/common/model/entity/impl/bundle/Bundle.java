package de.algorythm.cms.common.model.entity.impl.bundle;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.algorythm.cms.common.impl.jaxb.adapter.LocaleXmlAdapter;
import de.algorythm.cms.common.impl.jaxb.adapter.OutputMapXmlAdapter;
import de.algorythm.cms.common.impl.jaxb.adapter.UriXmlAdapter;
import de.algorythm.cms.common.model.entity.bundle.IBundle;
import de.algorythm.cms.common.model.entity.bundle.IOutputConfig;
import de.algorythm.cms.common.model.entity.bundle.OutputFormat;
import de.algorythm.cms.common.model.entity.impl.Page;

@XmlRootElement(name="bundle", namespace="http://cms.algorythm.de/common/Bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bundle implements IBundle {

	@XmlAttribute(required = true)
	private String title;
	@XmlJavaTypeAdapter(value = UriXmlAdapter.class)
	@XmlAttribute(required = true)
	@XmlSchemaType(name = "anyURI")
	private URI uri;
	@XmlJavaTypeAdapter(value = LocaleXmlAdapter.class, type = String.class)
	@XmlAttribute(name = "default-locale")
	private Locale defaultLocale = Locale.UK;
	@XmlJavaTypeAdapter(value = LocaleXmlAdapter.class)
	@XmlElementWrapper(name = "supported-locales", namespace = "http://cms.algorythm.de/common/Bundle")
	@XmlElements({
		@XmlElement(name = "locale", namespace = "http://cms.algorythm.de/common/Bundle")
	})
	private final Set<Locale> supportedLocales = new LinkedHashSet<>();
	@XmlJavaTypeAdapter(value = UriXmlAdapter.class)
	@XmlSchemaType(name = "anyURI")
	@XmlElementWrapper(name = "dependencies", namespace = "http://cms.algorythm.de/common/Bundle")
	@XmlElements({
		@XmlElement(name = "dependency", namespace = "http://cms.algorythm.de/common/Bundle")
	})
	private final Set<URI> dependencies = new LinkedHashSet<>();
	@XmlElement(name = "output-mapping", namespace = "http://cms.algorythm.de/common/Bundle", required = false)
	@XmlJavaTypeAdapter(OutputMapXmlAdapter.class)
	private final Map<OutputFormat, IOutputConfig> outputMapping = new HashMap<>();
	private Page startPage;

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	@Override
	public Set<Locale> getSupportedLocales() {
		return supportedLocales;
	}

	@Override
	public Page getStartPage() {
		return startPage;
	}

	public void setStartPage(Page startPage) {
		this.startPage = startPage;
	}

	@Override
	public Set<URI> getDependencies() {
		return dependencies;
	}

	@Override
	public Map<OutputFormat, IOutputConfig> getOutputMapping() {
		return outputMapping;
	}
}
