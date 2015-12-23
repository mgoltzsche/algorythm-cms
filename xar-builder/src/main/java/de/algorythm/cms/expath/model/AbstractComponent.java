package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.net.URI;

/**
 * Abstract expath package component description.
 * @see <a href="http://expath.org/spec/pkg#expath-pkg.xsd">http://expath.org/spec/pkg#expath-pkg.xsd</a>
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractComponent {

	static protected final String XQUERY = "xquery";
	static protected final String XSLT = "xslt";
	static protected final String XSD = "xsd";
	static protected final String RESOURCE = "resource";
	
    @XmlElement(name = "file", namespace = "http://expath.org/ns/pkg", required = true)
    @XmlSchemaType(name = "anyURI")
    private URI file;

    public AbstractComponent() {}

    public AbstractComponent(URI file) {
        this.file = file;
    }

    public abstract String getType();
    
    public abstract String getName();
    
    public URI getFile() {
        return file;
    }

    public void setFile(URI file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + file + "]";
    }
}
