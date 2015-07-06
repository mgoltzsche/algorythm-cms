package de.algorythm.cms.expath.model;

import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * Created by max on 31.05.15.
 */
@XmlRootElement(name="xslt", namespace="http://expath.org/ns/pkg")
@XmlAccessorType(XmlAccessType.FIELD)
public class XsltComponent extends Component {

    public XsltComponent() {}
    public XsltComponent(URI file, URI importUri) {
        super(file, importUri);
    }
}
