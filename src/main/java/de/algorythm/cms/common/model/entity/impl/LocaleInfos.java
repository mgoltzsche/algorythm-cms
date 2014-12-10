package de.algorythm.cms.common.model.entity.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="locales", namespace="http://cms.algorythm.de/common/Locales")
public class LocaleInfos {

	@XmlElementRef
	private final List<LocaleInfo> locales = new LinkedList<LocaleInfo>();

	public List<LocaleInfo> getLocales() {
		return locales;
	}
}
