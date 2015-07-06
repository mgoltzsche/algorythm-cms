package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * Created by max on 03.06.15.
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

    public URI getImportUri() {
        return importUri;
    }

    public void setImportUri(URI importUri) {
        this.importUri = importUri;
    }
}
