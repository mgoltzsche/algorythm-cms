package de.algorythm.cms.common.model.entity.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.IParam;

@XmlRootElement(name="param", namespace="http://cms.algorythm.de/common/Bundle")
public class Param extends AbstractMergeable implements IParam {

	@XmlAttribute(required = true)
	private String id;
	@XmlAttribute(required = true)
	private String value;

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	protected String getMergeableId() {
		return id;
	}

	@Override
	public String toString() {
		return "Param [" + id + " => " + value + ']';
	}
}
