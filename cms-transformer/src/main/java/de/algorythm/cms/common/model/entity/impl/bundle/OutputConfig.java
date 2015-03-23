package de.algorythm.cms.common.model.entity.impl.bundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.algorythm.cms.common.model.entity.bundle.IOutputConfig;
import de.algorythm.cms.common.model.entity.bundle.IModule;
import de.algorythm.cms.common.model.entity.bundle.ITheme;
import de.algorythm.cms.common.model.entity.bundle.OutputFormat;

@XmlRootElement(name="output", namespace="http://cms.algorythm.de/common/Bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputConfig implements IOutputConfig {

	@XmlAttribute(required = true)
	private OutputFormat format;
	@XmlElementRef(type = Theme.class)
	private ITheme theme;
	@XmlElementRef(type = Module.class)
	private IModule module;

	public OutputConfig() {}

	public OutputConfig(IOutputConfig src) {
		this(src.getFormat(), src.getTheme(), src.getModule());
	}

	public OutputConfig(OutputFormat format, ITheme theme, IModule module) {
		this.format = format;
		this.theme = theme;
		this.module = module;
	}
	
	@Override
	public OutputFormat getFormat() {
		return format;
	}

	public void setFormat(OutputFormat format) {
		this.format = format;
	}

	@Override
	public ITheme getTheme() {
		return theme;
	}

	public void setTheme(ITheme theme) {
		this.theme = theme;
	}

	@Override
	public IModule getModule() {
		return module;
	}

	public void setModule(IModule module) {
		this.module = module;
	}
}
