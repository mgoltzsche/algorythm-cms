package de.algorythm.cms.common;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.thaiopensource.relaxng.SchemaFactory;

import de.algorythm.cms.common.rendering.pipeline.impl.Cache;

public class TestRelaxNg {

	/*@Test
	public void testRelaxNg() throws Exception {
		final Schema schema = createSchema();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final InputStream stream = Files.newInputStream(classPathFile("/de/algorythm/cms/common/international/images/example.svg"));
		
		builder.parse(stream);
	}
	
	private Schema createSchema() {
		Path schemaFile = classPathFile("/test-repo/example1.org/international/types/svg/svg.xml")
		
		return new SchemaFactory().createSchema(new InputSource(schemaFile.toString()));
	}
	
	private Path classPathFile(String fileName) {
		final URL url = getClass().getResource(fileName);
		
		return FileSystems.getDefault().getPath(url.getPath());
	}*/
}
