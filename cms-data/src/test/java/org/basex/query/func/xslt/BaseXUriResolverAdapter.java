package org.basex.query.func.xslt;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;

import org.basex.core.Databases;
import org.basex.io.IO;
import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.StaticContext;
import org.basex.util.InputInfo;
import org.basex.util.QueryInput;
import org.w3c.dom.Node;

public class BaseXUriResolverAdapter implements URIResolver {

	/*private final UriResolver baseXUriResolver;
	
	public BaseXUriResolverAdapter(UriResolver baseXUriResolver) {
		this.baseXUriResolver = baseXUriResolver;
	}*/
	
	private final StaticContext sc;
	private final QueryContext qc;
	private final InputInfo info;
	
	public BaseXUriResolverAdapter(StaticContext sc, QueryContext qc, InputInfo info) {
		this.sc = sc;
		this.qc = qc;
		this.info = info;
	}
	
	@Override
	public Source resolve(String href, String baseUri) throws TransformerException {
		System.out.println("### " + href);
		
		//IO source;
		/*Source source;
		try {
			final Node node = qc.resources.doc(new QueryInput(href), sc.baseIO(), info).toJava();
			source = new DOMSource(node, href);
			//source = QueryResources.checkPath(new QueryInput(href), sc.baseIO(), info);
		} catch (QueryException e) {
			throw new TransformerException(e);
		}*/
		
		
		final IO base = sc.baseIO();
		IO source = base != null ? base.merge(href) : IO.get(href);
		//System.out.println("# " + source.dbname() + "  " + source.path());
		try {
			System.out.println("-> " + source.string());
		} catch (IOException e) {
			throw new TransformerException(e);
		}
		return null;
		//return source.streamSource();
		//return sc.resolve(href, baseUri).streamSource();
	}

}
