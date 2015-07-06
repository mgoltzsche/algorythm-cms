package de.algorythm.cms.expath;

import de.algorythm.cms.expath.model.Dependency;
import de.algorythm.cms.expath.model.ExpathPackage;
import de.algorythm.cms.expath.model.XQueryComponent;
import de.algorythm.cms.expath.model.XsltComponent;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import static org.junit.Assert.*;

/**
 * Created by max on 31.05.15.
 */
public class TestModel {

    @Test
    public void expath_model_should_be_marshallable_and_unmarshallable() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(ExpathPackage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        ExpathPackage pkg = new ExpathPackage(URI.create("http://example.org"), "exmpl", "Example package", "1.0");
        pkg.getDependencies().add(new Dependency("http://example.org/another-pkg1", "1.0"));
        pkg.getDependencies().add(new Dependency("http://example.org/another-pkg2", "1.0"));
        pkg.getComponents().add(new XQueryComponent(URI.create("hello-module.xqm"), URI.create("http://example.org/hello")));
        pkg.getComponents().add(new XsltComponent(URI.create("hello-module.xsl"), URI.create("http://example.org/hello.xsl")));

        marshaller.marshal(pkg, writer);
        System.out.println(writer);

        ExpathPackage unmarshalledPkg = (ExpathPackage) jaxbContext.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertEquals("package name", URI.create("http://example.org"), unmarshalledPkg.getName());
        assertEquals("dependencies", pkg.getDependencies().toString(), unmarshalledPkg.getDependencies().toString());
        assertEquals("components", pkg.getComponents().toString(), unmarshalledPkg.getComponents().toString());
    }
}
