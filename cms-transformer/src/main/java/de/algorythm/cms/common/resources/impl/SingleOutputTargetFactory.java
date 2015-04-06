package de.algorythm.cms.common.resources.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

public class SingleOutputTargetFactory implements IOutputTargetFactory, IOutputTarget {

	static private final Logger log = LoggerFactory.getLogger(SingleOutputTargetFactory.class);
	
	static private final IOutputTarget NULL_TARGET = new IOutputTarget() {
		@Override
		public boolean exists() {
			return false;
		}
		
		@Override
		public OutputStream createOutputStream() throws IOException {
			return new ByteArrayOutputStream();
		}
	};

	private final OutputStream out;
	private final String fileName;
	private boolean outputWritten;

	public SingleOutputTargetFactory(final OutputStream out, final String fileName) {
		this.out = out;
		this.fileName = fileName;
	}

	public boolean isOutputWritten() {
		return outputWritten;
	}

	@Override
	public IOutputTarget createOutputTarget(String publicPath) {
		if (!publicPath.isEmpty() && publicPath.charAt(0) != '/')
			publicPath = '/' + publicPath;
		
		if (publicPath.equals(fileName)) {
			outputWritten = true;
			
			return this;
		} else {
			log.debug("Output ommitted: " + publicPath);
			
			return NULL_TARGET;
		}
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public OutputStream createOutputStream() throws IOException {
		return out;
	}
}
