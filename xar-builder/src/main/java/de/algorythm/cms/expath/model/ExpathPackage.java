package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by max on 31.05.15.
 */
@XmlRootElement(name = "package", namespace = "http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExpathPackage {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private URI name;

    @XmlAttribute(required = true)
    private String abbrev;

    @XmlElement(name = "title", namespace = "http://expath.org/ns/pkg", required = true)
    private String title;

    @XmlAttribute(required = true)
    private String version;

    @XmlAttribute(required = true)
    private String spec = "1.0"; // Fixed value

    @XmlElements(value = {
            @XmlElement(name = "dependency", namespace = "http://expath.org/ns/pkg", type = Dependency.class),
    })
    private List<Dependency> dependencies = new LinkedList<>();

    @XmlElements(value = {
            @XmlElement(name = "resource", namespace = "http://expath.org/ns/pkg", type = Resource.class),
            @XmlElement(name = "xquery", namespace = "http://expath.org/ns/pkg", type = XQueryComponent.class),
            @XmlElement(name = "xslt", namespace = "http://expath.org/ns/pkg", type = XsltComponent.class)
    })
    private List<AbstractComponent> components = new LinkedList<>();

    public ExpathPackage() {}

    public ExpathPackage(URI name, String abbrev, String title, String version) {
        this.name = name;
        this.abbrev = abbrev;
        this.title = title;
        this.version = version;
    }

    public URI getName() {
        return name;
    }

    public void setName(URI name) {
        this.name = name;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public List<AbstractComponent> getComponents() {
        return components;
    }

    public void setComponents(List<AbstractComponent> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "Package [" + title + "]";
    }
}
