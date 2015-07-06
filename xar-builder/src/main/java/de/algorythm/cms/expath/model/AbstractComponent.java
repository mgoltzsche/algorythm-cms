package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.net.URI;

/**
 * Created by max on 31.05.15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractComponent {

    @XmlElement(name = "file", namespace = "http://expath.org/ns/pkg", required = true)
    @XmlSchemaType(name = "anyURI")
    private URI file;

    public AbstractComponent() {}

    public AbstractComponent(URI file) {
        this.file = file;
    }

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
