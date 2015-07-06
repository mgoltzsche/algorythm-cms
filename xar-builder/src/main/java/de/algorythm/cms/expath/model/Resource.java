package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * Created by max on 31.05.15.
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

    public URI getPublicUri() {
        return publicUri;
    }

    public void setPublicUri(URI publicUri) {
        this.publicUri = publicUri;
    }
}
