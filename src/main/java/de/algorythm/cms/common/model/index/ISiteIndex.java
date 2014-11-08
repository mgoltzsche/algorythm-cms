package de.algorythm.cms.common.model.index;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.algorythm.cms.common.model.entity.ISite;

public interface ISiteIndex {

	List<ISite> getSites() throws JAXBException, IOException, SAXException;
}
