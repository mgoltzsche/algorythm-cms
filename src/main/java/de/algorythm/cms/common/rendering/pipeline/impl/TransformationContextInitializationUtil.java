package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.algorythm.cms.common.resources.IUriResolver;
import de.algorythm.cms.common.resources.adapter.impl.CmsInputURIResolver;

public class TransformationContextInitializationUtil {
	
	static private final Logger log = LoggerFactory.getLogger(TransformationContextInitializationUtil.class);
	
	static public ErrorHandler ERROR_HANDLER = new ErrorHandler() {

		@Override
		public void warning(final SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void error(final SAXParseException exception) throws SAXException {
			throw new SAXException(exception.toString(), exception);
		}

		@Override
		public void fatalError(final SAXParseException exception) throws SAXException {
			throw new SAXException(exception.toString(), exception);
		}
	};
	
	static private final class TemplateDefinitionErrorListener implements ErrorListener {
		
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
	};
	
	static public Templates createTransformationTemplates(final Collection<URI> xslSourceUris, final IUriResolver uriResolver, final URI notFoundContent) {
		final Source xslSource = createMergedXslSource(xslSourceUris);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final TemplateDefinitionErrorListener errorListener = new TemplateDefinitionErrorListener();
		
		transformerFactory.setErrorListener(errorListener);
		transformerFactory.setURIResolver(new CmsInputURIResolver(uriResolver, notFoundContent));
		
		try {
			final Templates tpls = transformerFactory.newTemplates(xslSource);
			
			if (!errorListener.errors.isEmpty()) {
				final String msg = errorsToString("Errors:", errorListener.errors);
				
				throw new IllegalStateException("Cannot load XSL templates:" + msg);
			} else if (!errorListener.warnings.isEmpty()) {
				final String msg = errorsToString("Warnings:", errorListener.warnings);
				
				log.warn(msg);
			}
			
			return tpls;
		} catch (TransformerConfigurationException e) {
			final String msg = errorsToString("Errors:", errorListener.errors);
			
			throw new IllegalStateException("Cannot load XSL templates. " + msg, e);
		}
	}
	
	static private String errorsToString(String label, Iterable<TransformerException> errors) {
		final StringBuilder sb = new StringBuilder(label);
		
		for (TransformerException error : errors) {
			SourceLocator l = error.getLocator();
			sb.append("\n\t").append(l.getSystemId()).append(':')
				.append(l.getLineNumber()).append(':')
				.append(l.getColumnNumber()).append(" - ").append(error);
		}
		
		return sb.toString();
	}
	
	static public Source createMergedXslSource(final Collection<URI> xslSourceUris) {
		if (xslSourceUris.size() == 1)
			return new StreamSource(xslSourceUris.iterator().next().toString());
		
		final StringBuilder xslt = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		
		for (URI sourceUri : xslSourceUris) {
			xslt.append("\n<xsl:import href=\"")
				.append(StringEscapeUtils.escapeXml(sourceUri.toString()))
				.append("\" />");
		}
		
		final String mergedXsl = xslt.append("</xsl:stylesheet>").toString();
		final Reader mergedTplReader = new StringReader(mergedXsl);
		
		return new StreamSource(mergedTplReader);
	}
}
