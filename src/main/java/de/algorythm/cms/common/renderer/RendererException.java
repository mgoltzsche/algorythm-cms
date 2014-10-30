package de.algorythm.cms.common.renderer;

public class RendererException extends Exception {

	static private final long serialVersionUID = 3800676176609318186L;

	public RendererException(final Exception e) {
		super(e.getMessage(), e);
	}
	
	public RendererException(final String msg, final Exception e) {
		super(msg, e);
	}
	
	public RendererException(final String msg) {
		super(msg);
	}
}
