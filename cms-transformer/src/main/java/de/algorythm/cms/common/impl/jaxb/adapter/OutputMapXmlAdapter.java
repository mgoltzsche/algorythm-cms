package de.algorythm.cms.common.impl.jaxb.adapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.algorythm.cms.common.model.entity.bundle.Format;
import de.algorythm.cms.common.model.entity.impl.bundle.OutputConfig;
import de.algorythm.cms.common.model.entity.impl.bundle.OutputConfigContainer;

public class OutputMapXmlAdapter extends XmlAdapter<OutputConfigContainer, Map<Format, OutputConfig>> {

	@Override
	public Map<Format, OutputConfig> unmarshal(OutputConfigContainer configs) throws Exception {
		final Map<Format, OutputConfig> map = new HashMap<>();
		
		for (OutputConfig outputConfig : configs.getOutputs())
			map.put(outputConfig.getFormat(), outputConfig);
		
		return map;
	}

	@Override
	public OutputConfigContainer marshal(Map<Format, OutputConfig> configs) throws Exception {
		return new OutputConfigContainer(new LinkedList<>(configs.values()));
	}

}
