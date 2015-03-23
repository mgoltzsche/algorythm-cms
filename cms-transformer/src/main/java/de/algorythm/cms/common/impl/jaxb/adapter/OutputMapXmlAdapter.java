package de.algorythm.cms.common.impl.jaxb.adapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.algorythm.cms.common.model.entity.bundle.OutputFormat;
import de.algorythm.cms.common.model.entity.impl.bundle.OutputConfig;
import de.algorythm.cms.common.model.entity.impl.bundle.OutputConfigContainer;

public class OutputMapXmlAdapter extends XmlAdapter<OutputConfigContainer, Map<OutputFormat, OutputConfig>> {

	@Override
	public Map<OutputFormat, OutputConfig> unmarshal(OutputConfigContainer configs) throws Exception {
		final Map<OutputFormat, OutputConfig> map = new HashMap<>();
		
		for (OutputConfig outputConfig : configs.getOutputs())
			map.put(outputConfig.getFormat(), outputConfig);
		
		return map;
	}

	@Override
	public OutputConfigContainer marshal(Map<OutputFormat, OutputConfig> configs) throws Exception {
		return new OutputConfigContainer(new LinkedList<>(configs.values()));
	}

}
