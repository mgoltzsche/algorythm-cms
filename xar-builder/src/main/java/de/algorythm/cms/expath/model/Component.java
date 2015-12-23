package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * Abstract expath package component description.
 * @see <a href="http://expath.org/spec/pkg#expath-pkg.xsd">http://expath.org/spec/pkg#expath-pkg.xsd</a>
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Component extends AbstractComponent {

    @XmlElement(name = "import-uri", namespace = "http://expath.org/ns/pkg", required = true)
    @XmlSchemaType(name = "anyURI")
    private URI importUri;

    public Component() {}

    public Component(URI file, URI importUri) {
        super(file);
        this.importUri = importUri;
    }

    @Override
    public final String getName() {
    	return importUri.toASCIIString();
    }
   
    public URI getImportUri() {
        return importUri;
    }

    public void setImportUri(URI importUri) {
        this.importUri = importUri;
    }
}
