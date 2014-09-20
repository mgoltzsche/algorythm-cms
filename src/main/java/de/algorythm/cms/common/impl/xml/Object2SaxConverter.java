package de.algorythm.cms.common.impl.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.xml.sax.ContentHandler;

public class Object2SaxConverter {

	private final JAXBContext jaxbContext;
	private final Marshaller marshaller;
	
	public Object2SaxConverter(Class<?>... classes) throws JAXBException {
		jaxbContext = JAXBContext.newInstance(classes);
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	}
	
	public void marshal(final Object obj, final ContentHandler handler) throws JAXBException {
		marshaller.marshal(obj, handler);
	}
	
	public void marshal(final Object obj, final File outputFile) throws JAXBException {
		marshaller.marshal(obj, outputFile);
	}
}
