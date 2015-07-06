package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * Created by max on 31.05.15.
 */
@XmlRootElement(name="xsd", namespace="http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class XsdComponent extends Component {

    public XsdComponent() {}
    public XsdComponent(URI file, URI importUri) {
        super(file, importUri);
    }
}
