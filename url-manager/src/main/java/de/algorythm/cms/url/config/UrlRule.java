package de.algorythm.cms.url.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="rule", namespace="http://algorythm.de/common/URLRules")
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlRule {

	@XmlAttribute(required = true)
	private String key;
	@XmlAttribute(required = true)
	private String pattern;
	@XmlValue
	private String command;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return key + ':' + pattern + ':' + command;
	}
}
