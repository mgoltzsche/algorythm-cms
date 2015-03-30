package de.algorythm.cms.common.rendering.pipeline.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateErrorListener implements ErrorListener {
	
	static private final Logger log = LoggerFactory.getLogger(TemplateErrorListener.class);
	
	private List<TransformerException> warnings = new LinkedList<TransformerException>();
	private List<TransformerException> errors = new LinkedList<TransformerException>();
	
	@Override
	public void warning(TransformerException exception)
			throws TransformerException {
		warnings.add(exception);
	}
	
	@Override
	public void fatalError(TransformerException exception)
			throws TransformerException {
		errors.add(exception);
	}
	
	@Override
	public void error(TransformerException exception)
			throws TransformerException {
		errors.add(exception);
	}
	
	public void evaluateErrors() {
		if (!errors.isEmpty()) {
			final String msg = errorsToString("Errors:", errors);
			
			throw new IllegalStateException(msg);
		} else if (!warnings.isEmpty()) {
			final String msg = errorsToString("Warnings:", warnings);
			
			log.warn(msg);
		}
	}
	
	@Override
	public String toString() {
		return errorsToString("Error:", errors);
	}
	
	private String errorsToString(final String label, final Iterable<TransformerException> errors) {
		final StringBuilder sb = new StringBuilder(label);
		
		for (TransformerException error : errors) {
			SourceLocator l = error.getLocator();
			
			if (l != null) {
				sb.append("\n\t").append(l.getSystemId()).append(':')
					.append(l.getLineNumber()).append(':')
					.append(l.getColumnNumber()).append(" - ");
			}
			
			sb.append(error);
			
			StringBuilder indent = new StringBuilder("\n\t");
			Throwable cause = error;
			
			while((cause = cause.getCause()) != null) {
				indent.append("\t");
				sb.append(indent).append(cause);
			}
		}
		
		return sb.toString();
	}
}