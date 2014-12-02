package de.algorythm.cms.common.generator;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.Configuration;
import de.algorythm.cms.common.model.entity.IPage;

@Singleton
public class PagesXmlGenerator {

	private final JAXBContext jaxbContext;
	
	@Inject
	public PagesXmlGenerator(final Configuration cfg, final JAXBContext jaxbContext) throws JAXBException {
		this.jaxbContext = jaxbContext;
	}
	
	public void generatePagesXml(final IPage startPage, final File outputDirectory) throws JAXBException {
		final Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(startPage, new File(outputDirectory, "pages.xml"));
	}
}
