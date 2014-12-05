package de.algorythm.cms.common.rendering.pipeline.impl;

public class RenderingException extends Exception {

	static private final long serialVersionUID = 3800676176609318186L;

	public RenderingException(final Exception e) {
		super(e.getMessage(), e);
	}
	
	public RenderingException(final String msg, final Exception e) {
		super(msg, e);
	}
	
	public RenderingException(final String msg) {
		super(msg);
	}
}
