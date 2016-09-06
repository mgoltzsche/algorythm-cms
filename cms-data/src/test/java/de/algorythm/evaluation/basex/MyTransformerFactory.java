package de.algorythm.evaluation.basex;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import net.sf.saxon.TransformerFactoryImpl;

public class MyTransformerFactory extends TransformerFactory {

	private final TransformerFactory delegate;

	public MyTransformerFactory() {
		this.delegate = new TransformerFactoryImpl();
	}
	
	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public Transformer newTransformer(Source source)
			throws TransformerConfigurationException {
		return delegate.newTransformer(source);
	}

	public Transformer newTransformer()
			throws TransformerConfigurationException {
		return delegate.newTransformer();
	}

	public Templates newTemplates(Source source)
			throws TransformerConfigurationException {
		return delegate.newTemplates(source);
	}

	public Source getAssociatedStylesheet(Source source, String media,
			String title, String charset)
			throws TransformerConfigurationException {
		return delegate.getAssociatedStylesheet(source, media, title, charset);
	}

	public String toString() {
		return delegate.toString();
	}

	public void setURIResolver(URIResolver resolver) {
		delegate.setURIResolver(resolver);
	}

	public URIResolver getURIResolver() {
		throw new IllegalStateException("###"+delegate.getURIResolver().getClass());
		//return delegate.getURIResolver();
	}

	public void setFeature(String name, boolean value)
			throws TransformerConfigurationException {
		delegate.setFeature(name, value);
	}

	public boolean getFeature(String name) {
		return delegate.getFeature(name);
	}

	public void setAttribute(String name, Object value) {
		delegate.setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		return delegate.getAttribute(name);
	}

	public void setErrorListener(ErrorListener listener) {
		delegate.setErrorListener(listener);
	}

	public ErrorListener getErrorListener() {
		return delegate.getErrorListener();
	}
	
}
