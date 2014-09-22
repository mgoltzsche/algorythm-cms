package de.algorythm.cms.common.model.index;

import java.util.List;

import org.xml.sax.SAXException;

import de.algorythm.cms.common.model.entity.ISite;

public interface ISiteIndex {

	List<ISite> getSites() throws SAXException;
}
