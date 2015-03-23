package de.algorythm.cms.common.model.entity.impl.bundle;

import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="output-mapping", namespace="http://cms.algorythm.de/common/Bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputConfigContainer {

	@XmlElement(name = "output", namespace="http://cms.algorythm.de/common/Bundle")
	private final Collection<OutputConfig> outputs;
	
	public OutputConfigContainer() {
		outputs = new LinkedList<>();
	};
	
	public OutputConfigContainer(Collection<OutputConfig> outputs) {
		this.outputs = outputs;
	}

	public Collection<OutputConfig> getOutputs() {
		return outputs;
	}
}
