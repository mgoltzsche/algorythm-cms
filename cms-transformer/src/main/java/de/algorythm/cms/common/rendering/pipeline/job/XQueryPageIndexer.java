package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.inject.Singleton;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;

import org.apache.commons.io.output.ByteArrayOutputStream;

import de.algorythm.cms.common.resources.IInputResolver;
import de.algorythm.cms.common.resources.IOutputTargetFactory;

@Singleton
public class XQueryPageIndexer {

	static private final URI ROOT = URI.create("file:/");
	
	public void executeXQuery(final URI docUri, final URI xqueryUri,
			final IInputResolver resolver, final IOutputTargetFactory outFactory)
			throws Exception {
		final Configuration cfg = new Configuration();
		cfg.setXIncludeAware(false);
		final Processor processor = new Processor(cfg);
		final XQueryCompiler compiler = processor.newXQueryCompiler();
		final XQueryExecutable query;
		
		compiler.setEncoding(StandardCharsets.UTF_8.name());
		compiler.setBaseURI(ROOT.resolve(xqueryUri));
		
		try (InputStream xqueryStream = resolver.createInputStream(xqueryUri)) {
			query = compiler.compile(xqueryStream);
		}
		
		final XQueryEvaluator eval = query.load();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		//try (OutputStream out = outFactory.createOutputTarget(docUri.getPath()).createOutputStream()) {
		try (InputStream in = resolver.createInputStream(docUri)) {
			final Serializer serializer = processor.newSerializer(out);
			
			eval.runStreamed(new StreamSource(in), serializer);
		}
		
		System.out.println(out.toString(StandardCharsets.UTF_8.name()));
	}
}
