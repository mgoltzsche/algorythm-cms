package de.algorythm.cms.common.rendering.pipeline.job;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class XQueryPageIndexer implements IRenderingJob {

	@Override
	public void run(IRenderingContext context) throws Exception {
		/*Configuration cfg = new Configuration();
		cfg.setXIncludeAware(false);
		Processor processor = new Processor(cfg);
        XQueryCompiler compiler = processor.newXQueryCompiler();
        XQueryExecutable query = compiler.compile(xqueryStream);
        XQueryEvaluator eval = query.load();
        
        eval.runStreamed(new StreamSource(docStream), new SAXDestination(handler));*/
	}

}
