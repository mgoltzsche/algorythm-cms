package de.algorythm.cms.url.config;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;

@XmlRootElement(name="configuration", namespace="http://algorythm.de/common/URLRules")
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlConfiguration {

	static public UrlConfiguration fromStream(InputStream stream) throws XMLStreamException, JAXBException {
		return new UrlConfigurationLoader().loadConfiguration(stream);
	}
	
	@XmlElements({@XmlElement(name = "rule", namespace = "http://algorythm.de/common/URLRules", type = UrlRule.class)})
	private List<UrlRule> rules;

	public List<UrlRule> getRules() {
		return rules;
	}

	public void setRules(List<UrlRule> rules) {
		this.rules = rules;
	}
	
	@Override
	public String toString() {
		return "UrlConfiguration " + rules;
	}
}
