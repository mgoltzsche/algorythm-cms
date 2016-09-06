package de.algorythm.evaluation.basex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.xml.transform.TransformerFactory;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.basex.api.client.LocalSession;
import org.basex.api.client.Query;
import org.basex.api.client.Session;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.Delete;
import org.basex.core.cmd.Flush;
import org.basex.core.cmd.InfoStorage;
import org.basex.core.cmd.RepoList;
import org.basex.core.cmd.XQuery;
import org.basex.io.IOContent;
import org.basex.io.serial.SerializerOptions;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.junit.Test;

public class TestBaseX {
	
	static private final String QUERY = "for $i in 1 to 10 return <xml>Text {$i}</xml>";

	@Test
	public void test() throws Exception {
		//System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		//System.setProperty("javax.xml.transform.TransformerFactory", "de.algorythm.evaluation.basex.MyTransformerFactory");
		
		System.setProperty("org.basex.CHOP", "false"); // preserves white spaces
		System.setProperty("org.basex.DTD", "true");
		System.setProperty("org.basex.CATFILE", "/home/max/BaseXRepo/catalog.xml");
		System.setProperty("org.basex.DEBUG", "true");
		System.setProperty("http://saxon.sf.net/feature/uriResolverClass", "org.apache.xml.resolver.tools.CatalogResolver");
		OutputStream out = System.out;
		Context context = new Context();
		
		//context.options.set(new BooleanOption("CHOP"), false);
		//context.repo.pkgDict().get();

		try (Session session = new LocalSession(context, out)) {// new ClientSession(host, port, username, password)
			InputStream in = new ByteArrayInputStream("<x>database entry</x>".getBytes(StandardCharsets.UTF_8));
			
			session.execute("SET CATFILE /home/max/BaseXRepo/catalog.xml");
			session.execute("SET DTD true");
			
			createDatabase(session, in);
			saveXml(session, "example-entry1", "<y>1st entry from DB</y>");
			saveXml(session, "example-entry2", "<y>2nd entry from DB</y>");
			saveXml(session, "category/example-cat.xml", "<!DOCTYPE category PUBLIC \"-//algorythm//Category\" \"http://algorythm.de/category.dtd\"><x>Category</x>");
			saveXml(session, "doc.xml", "<doc title='My article'><p>This is a <b>test</b>!</p></doc>");
			query1(session);
			query2(session);
			query3(session);
			query4(context);
			queryWithExternalVariable(session);
			queryDocumentFromExternalFile(session);
			queryDocumentFromDB(session);
			queryCollection(session);
			queryPath(session);
			storeFile(session);
			readFile(session);
			session.execute(new InfoStorage());
			delete(session);
			session.execute(new InfoStorage());
			updateDocument(session);
			session.execute(new Flush());
			xslTransform(session);
			installCustomXQueryModule(session);
			callCustomXQueryModule(session);
			installCustomPackage(session);
			session.execute(new RepoList());
			session.execute(new InfoStorage());
			showRepoNamespaces(context);
			callCustomPackageXQuery(session);
			showXsltProcessor(session);
			callCustomPackageXSLT(session); // Doesn't work
			callFnTransform(context);
			callFnTransformUsingImport(session);
			session.close();
			
			TransformerFactory f = TransformerFactory.newInstance();
			f.setURIResolver(new CatalogResolver()); // TODO: Custom resolver that consults CatalogResolver if URI contains protocol else delegates to default basex resolver (is there one?) 
		} finally {
			context.close();
		}
	}
	
	private void createDatabase(Session session, InputStream in) throws IOException {
		session.create("mydatabase", in);
		System.out.println(session.info());
	}
	
	private void saveXml(Session session, String name, String xml) throws IOException {
		session.execute(new Add(name, xml));
		System.out.println(session.info());
	}

	private void query1(Session session) throws IOException {
		System.out.println("QUERY 1");
		
		try (Query query = session.query(QUERY)) {
			query.execute();
			System.out.println(query.info());
		}
	}

	private void query2(Session session) throws IOException {
		System.out.println("QUERY 2");
		
		try (Query query = session.query(QUERY)) {
			while (query.more())
				query.next();
			
			System.out.println(query.info());
		}
	}

	private void query3(Session session) throws IOException {
		System.out.println("QUERY 3");
		long start = System.nanoTime();
		session.execute(new XQuery(QUERY));
		double end = (System.nanoTime() - start) / 1000000.0;
		System.out.println(session.info());
		System.out.println(String.format("Query executed in %.2f ms", end));
	}
	
	private void query4(Context context) throws QueryException {
		System.out.println("QUERY 4 DIRECTLY WITH CONTEXT");
		long start = System.nanoTime();
	    // Create a query processor
	    try(QueryProcessor proc = new QueryProcessor(QUERY, context)) {
	      // Execute the query
	      Value result = proc.value();

	      // Print result as string.
	      System.out.println(result);
	    }
	    double end = (System.nanoTime() - start) / 1000000.0;
	    System.out.println(String.format("Query executed in %.2f ms", end));
	  }

	private void queryWithExternalVariable(Session session) throws IOException {
		System.out.println("QUERY WITH EXTERNAL VARIABLE");
		XQuery xquery = new XQuery("declare variable $name external := ''; 'Hello ' || $name || '!'");
		xquery.bind("name", "John");
		session.execute(xquery);
		System.out.println(session.info());
	}

	private void delete(Session session) throws IOException {
		session.execute(new Delete("example-entry2.xml"));
		System.out.println(session.info());
	}
	
	private void queryDocumentFromExternalFile(Session session) throws Exception {
		System.out.println("QUERY EXTERNAL FILE");
		String path = getClass().getResource("/example-files/example-file.xml").toURI().getPath();
		xquery(session, "doc('" + path + "')");
	}

	private void queryDocumentFromDB(Session session) throws Exception {
		System.out.println("QUERY FROM DB");
		xquery(session, "doc('mydatabase/doc.xml')");
	}

	private void queryCollection(Session session) throws IOException {
		System.out.println("QUERY COLLECTION");
		xquery(session, "for $doc in collection('mydatabase')\n" +
				"return document-uri($doc)");
	}

	private void queryPath(Session session) throws IOException {
		xquery(session, "/y/text()");
	}

	private void updateDocument(Session session) throws IOException {
		System.out.println("UPDATE");
		xquery(session, "replace value of node doc('mydatabase/doc.xml')/doc/@title with 'My new title'");
		xquery(session, "doc('mydatabase/doc.xml')");
		xquery(session, "replace value of node doc('mydatabase/doc.xml')/doc/@title with 'My newer title'");
		xquery(session, "doc('mydatabase/doc.xml')");
	}

	private void storeFile(Session session) throws IOException {
		System.out.println("STORE FILE");
		session.execute("STORE TO myfiles/ " + getClass().getResource("/example-files/test-file.txt").getPath());
	}

	private void readFile(Session session) throws IOException {
		System.out.println("READ FILE");
		session.execute("RETRIEVE myfiles/test-file.txt");
	}

	private void xslTransform(Session session) throws Exception {
		System.out.println("XSLT");
		String xslFilePath = "/example-files/example-transformation.xsl";

		String path = getClass().getResource(xslFilePath).toURI().getPath();
		xquery(session, "xslt:transform(doc('mydatabase/doc.xml'), '" + path + "')");
		xquery(session, "xslt:transform(doc('mydatabase/doc.xml'), '" + path + "')");

		try (InputStream xsl = getClass().getResourceAsStream(xslFilePath)) {
			session.add("transformations/example-transformation.xsl", xsl);
		}

		xquery(session, "xslt:transform(doc('mydatabase/doc.xml'), doc('mydatabase/transformations/example-transformation.xsl'))");
		xquery(session, "xslt:transform(doc('mydatabase/doc.xml'), doc('mydatabase/transformations/example-transformation.xsl'))");
	}

	private void installCustomXQueryModule(Session session) throws Exception {
		System.out.println("INSTALL CUSTOM XQUERY MODULE");
		session.execute("REPO INSTALL " + getClass().getResource("/example-import-directory/xq/test-module-v1.xqm").getPath());
		// Try 2nd time to show 1st version is overridden
		session.execute("REPO INSTALL " + getClass().getResource("/example-import-directory/xq/test-module-v2.xqm").getPath());
	}

	private void callCustomXQueryModule(Session session) throws Exception {
		System.out.println("CALL CUSTOM XQUERY MODULE");
		xquery(session, "import module namespace m = 'http://algorythm.de/cms/example/Hello';\n" +
				"m:hello('Universe')");
	}

	private void installCustomPackage(Session session) throws Exception {
		System.out.println("INSTALL CUSTOM PACKAGE");
		session.execute("REPO INSTALL " + getClass().getResource("/").toURI().resolve("../../../xar-builder/target/test-classes/test.xar"));
		session.execute("REPO INSTALL " + getClass().getResource("/functx-1.0.xar"));
	}

	private void showRepoNamespaces(Context context) throws Exception {
		for (byte[] name : context.repo.nsDict()) {
			System.out.println("NS: " + new String(name));
			Iterator<byte[]> tokenIter = context.repo.nsDict().get(name).iterator();
			while(tokenIter.hasNext()) {
				System.out.println("    " + new String(tokenIter.next()));
			}
		}
	}
	
	private void callCustomPackageXQuery(Session session) throws Exception {
		System.out.println("CALL CUSTOM PACKAGE XQUERY");
		xquery(session, "import module namespace m = 'http://example.org/hello';\n" +
				"m:hello('from custom package')");
	}

	private void showXsltProcessor(Session session) throws Exception {
		xquery(session, "concat(xslt:processor(), ', XSLT ', xslt:version())");
	}

	private void callCustomPackageXSLT(Session session) throws Exception {
		System.out.println("CALL CUSTOM PACKAGE XSLT");
		/*xquery(session, "declare option output:method 'html';\n" +
				"declare option db:chop 'no';\n" +
				"let $xml := <doc>hello <b>world</b> with XSLT</doc>\n" +
				"let $style := <xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:import href=\"http://example.org/xml2txt.xsl\" /><xsl:template match='/'><root>root</root></xsl:template></xsl:stylesheet>\n" +
				//"let $style := 'http://example.org/xml2txt.xsl'" +
				"return xslt:transform($xml, $style)");*/
		/*xquery(session, "import module namespace m = 'http://example.org/hello';\n" +
			"let $xml := <doc>hello <b>world</b> with XSLT from package</doc>\n" +
			"return m:asText($xml)");*/
		xquery(session, "let $xml := <doc>hello <b>world</b> with XSLT</doc>\n" +
				"let $style := <xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" "
				+ "xmlns:functx=\"http://www.functx.com\" "
				+ "xmlns:exmpl=\"http://example.org/hello\">"
				+ "<xsl:import href=\"http://www.functx.com/functx.xsl\" />"
				+ "<xsl:import href=\"http://example.org/hello-module.xsl\" />"
				+ "<xsl:template match=\"*\"><doc><h><xsl:value-of select=\"exmpl:path('universe')\" /></h> <xsl:value-of select=\"text()\"/><xsl:apply-templates select=\"*\"/></doc></xsl:template>"
				+ "<xsl:template match=\"b\"><entry>XPath: <xsl:value-of select=\"functx:dynamic-path(/, 'doc/b')\" /></entry></xsl:template>"
				//+ "<xsl:template match=\"*\"><docs><xsl:copy-of select=\"document('mydatabase/example-entry1.xml')\"/></docs></xsl:template>"
				+ "</xsl:stylesheet>\n" +
				"return xslt:transform($xml, $style)");
	}

	private void callFnTransform(Context context) throws Exception {
		System.out.println("CALL FN:TRANSFORM");
		
		String xquery = "declare variable $user external := \"Anonymous\";\n"
				+ "let $xml := <doc>hello <b>world</b> with XSLT</doc>\n"
				+ "let $style := <xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
				+ "<xsl:template match=\"*\"><result>{$user}</result></xsl:template>"
				+ "</xsl:stylesheet>\n"
				+ "return fn:transform(map{'source-node': $xml, 'stylesheet-node': $style})";
		
		try(QueryProcessor proc = new QueryProcessor(xquery, context)) {
			proc.bind("user", "John");
			proc.compile();
			Value result = proc.value();
			System.out.println(new IOContent(result.serialize(SerializerOptions.get(false)).finish()).string());
			proc.bind("user", "Mustermann");
			result = proc.value();
			System.out.println(new IOContent(result.serialize(SerializerOptions.get(false)).finish()).string());
		}
	}
	
	private void callFnTransformUsingImport(Session session) throws Exception {
		System.out.println("CALL FN:TRANSFORM USING IMPORT");
		
		xquery(session, "let $xml := <doc>hello <b>world</b> with XSLT</doc>\n" +
				"let $style := <xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" "
				+ "xmlns:functx=\"http://www.functx.com\">"
				+ "<xsl:import href=\"http://www.functx.com/functx.xsl\" />"
				+ "<xsl:template match=\"*\"><doc><xsl:value-of select=\"text()\"/><xsl:apply-templates select=\"*\"/></doc></xsl:template>"
				+ "<xsl:template match=\"b\"><entry>XPath: <xsl:value-of select=\"functx:dynamic-path(/, 'doc/b')\" /></entry></xsl:template>"
				+ "</xsl:stylesheet>\n" +
				"return fn:transform(map{'source-node': $xml, 'stylesheet-node': $style})");
	}

	private void xquery(Session session, String xquery) throws IOException {
		System.out.println(xquery);
		System.out.print("OUTPUT: ");
		session.execute(new XQuery(xquery));
		session.info();
		System.out.println();
	}
}
