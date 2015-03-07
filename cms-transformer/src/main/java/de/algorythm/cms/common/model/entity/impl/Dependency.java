package de.algorythm.cms.common.model.entity.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IDependency;

@XmlRootElement(name="dependency", namespace="http://cms.algorythm.de/common/Bundle")
public class Dependency implements IDependency {

	@XmlAttribute(name = "name", required = true)
	private String name;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
