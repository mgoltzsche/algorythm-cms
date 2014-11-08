@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters({
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value = de.algorythm.cms.common.impl.jaxb.adapter.LocaleXmlAdapter.class, type = java.util.Locale.class),
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value = de.algorythm.cms.common.impl.jaxb.adapter.UriXmlAdapter.class, type = java.net.URI.class)
})
package de.algorythm.cms.common.model.entity.impl;