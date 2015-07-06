package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;

/**
 * Created by max on 31.05.15.
 */
@XmlRootElement(name="dependency", namespace="http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dependency {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String name;

    @XmlAttribute
    private String version;

    @XmlAttribute
    private String versions;

    @XmlAttribute
    private String semver;

    @XmlAttribute(name = "semver-min")
    private String semverMin;

    @XmlAttribute(name = "semver-max")
    private String semverMax;

    public Dependency() {}

    public Dependency(String name, String versions) {
        this.name = name;
        this.versions = versions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public String getSemver() {
        return semver;
    }

    public void setSemver(String semver) {
        this.semver = semver;
    }

    public String getSemverMin() {
        return semverMin;
    }

    public void setSemverMin(String semverMin) {
        this.semverMin = semverMin;
    }

    public String getSemverMax() {
        return semverMax;
    }

    public void setSemverMax(String semverMax) {
        this.semverMax = semverMax;
    }

    @Override
    public String toString() {
        return "Dependency [" + name + ":" + versions + "]";
    }
}
