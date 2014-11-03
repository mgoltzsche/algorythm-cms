package de.algorythm.cms.common.renderer.impl.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XslErrorListener implements ErrorListener {

	static private final Logger log = LoggerFactory.getLogger(XslErrorListener.class);
	static public final XslErrorListener INSTANCE = new XslErrorListener();
	
	
	private XslErrorListener() {}
	
	@Override
	public void error(TransformerException e) throws TransformerException {
		log.error("XSLT error", e);
	}

	@Override
	public void fatalError(TransformerException e) throws TransformerException {
		log.error("Fatal XSLT error", e);
	}

	@Override
	public void warning(TransformerException e) throws TransformerException {
		log.warn("XSLT warning", e);
	}
}
