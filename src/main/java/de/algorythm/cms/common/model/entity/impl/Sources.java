package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.ISource;

@XmlRootElement(name="sources", namespace="http://cms.algorythm.de/common/Sources")
public class Sources {

	@XmlElementRef(type = Source.class)
	private final List<ISource> sources = new LinkedList<ISource>();

	public List<ISource> getSources() {
		return sources;
	}
}
