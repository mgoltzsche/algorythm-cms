package de.algorythm.cms.common.generator;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.model.entity.ISite;

public class PagesXmlGenerator {

	private final File repositoryDirectory;
	private final Marshaller marshaller;
	
	public PagesXmlGenerator(final Configuration cfg, final JAXBContext jaxbContext) throws JAXBException {
		this.marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		this.repositoryDirectory = cfg.repository;
	}
	
	public void generatePagesXml(final ISite site) throws JAXBException {
		marshaller.marshal(site.getStartPage(), new File(new File(repositoryDirectory, site.getName()), "pages.xml"));
	}
}
