package de.algorythm.cms.common.renderer.impl.xslt;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class XslErrorListener implements ErrorListener {

	static public final XslErrorListener INSTANCE = new XslErrorListener();
	
	
	private XslErrorListener() {}
	
	@Override
	public void error(TransformerException e) throws TransformerException {
		throw e;
	}

	@Override
	public void fatalError(TransformerException e) throws TransformerException {
		throw e;
	}

	@Override
	public void warning(TransformerException e) throws TransformerException {
		throw e;
	}
}
