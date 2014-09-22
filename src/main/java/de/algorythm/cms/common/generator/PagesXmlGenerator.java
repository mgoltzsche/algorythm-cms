package de.algorythm.cms.common.generator;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.model.entity.ISite;

@Singleton
public class PagesXmlGenerator {

	private final JAXBContext jaxbContext;
	private final File repositoryDirectory;
	
	@Inject
	public PagesXmlGenerator(final Configuration cfg, final JAXBContext jaxbContext) throws JAXBException {
		this.jaxbContext = jaxbContext;
		this.repositoryDirectory = cfg.repository;
	}
	
	public void generatePagesXml(final ISite site) throws JAXBException {
		final Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(site.getStartPage(), new File(new File(repositoryDirectory, site.getName()), "pages.xml"));
	}
}
