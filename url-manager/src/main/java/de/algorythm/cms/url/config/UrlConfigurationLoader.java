package de.algorythm.cms.url.config;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class UrlConfigurationLoader {

	private final JAXBContext ctx;
	private final XMLInputFactory factory;
	
	public UrlConfigurationLoader() throws JAXBException {
		ctx = JAXBContext.newInstance(UrlConfiguration.class);
		factory = XMLInputFactory.newInstance();
	}
	
	public UrlConfiguration loadConfiguration(InputStream configXml) throws XMLStreamException, JAXBException {
		final XMLStreamReader in = factory.createXMLStreamReader(configXml);
		
		try {
			return ctx.createUnmarshaller().unmarshal(in, UrlConfiguration.class).getValue();
		} finally {
			in.close();
		}
	}
}
