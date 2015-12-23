package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * expath package resource component description.
 * @see <a href="http://expath.org/spec/pkg#expath-pkg.xsd">http://expath.org/spec/pkg#expath-pkg.xsd</a>
 * @author Max Goltzsche <max.goltzsche@algorythm.de> 2015-08, BSD License
 */
@XmlRootElement(name="resource", namespace="http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource extends AbstractComponent {

    @XmlElement(name = "public-uri", namespace = "http://expath.org/ns/pkg", required = true)
    @XmlSchemaType(name = "anyURI")
    private URI publicUri;

    public Resource() {}

    public Resource(URI file, URI publicUri) {
        super(file);
        this.publicUri = publicUri;
    }

    @Override
    public String getType() {
    	return RESOURCE;
    }

    @Override
    public final String getName() {
    	return publicUri.toASCIIString();
    }
   
    public URI getPublicUri() {
        return publicUri;
    }

    public void setPublicUri(URI publicUri) {
        this.publicUri = publicUri;
    }
}
