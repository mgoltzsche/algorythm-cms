package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import com.google.inject.Injector;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;
import de.algorythm.cms.common.scheduling.IProcess;
import de.algorythm.cms.common.scheduling.IProcessObserver;
import de.algorythm.cms.common.scheduling.IProgressObserver;

public class RenderingProcess implements IProcess, IRenderingContext {

	static private final Logger log = LoggerFactory.getLogger(RenderingProcess.class);
	
	static private class PipelinePhase {
		
		public final LinkedList<IRenderingJob> jobs;
		public int pendingSize;
		
		public PipelinePhase(final LinkedList<IRenderingJob> jobs) {
			this.jobs = jobs;
			this.pendingSize = jobs.size();
		}
	}
	
	private final Injector injector;
	private final IBundleRenderingContext context;
	private final Iterator<PipelinePhase> phaseIter;
	private PipelinePhase currentPhase;
	private final IProgressObserver<Void> observer;
	private final long startTime = System.currentTimeMillis();

	public RenderingProcess(final IBundleRenderingContext context, final List<Collection<IRenderingJob>> jobPhases, final Injector injector, final IProgressObserver<Void> observer) {
		if (jobPhases.isEmpty())
			throw new IllegalArgumentException("No jobs to execute");
		
		this.context = context;
		this.observer = observer;
		final LinkedList<PipelinePhase> phases = new LinkedList<PipelinePhase>();
		
		for (Collection<IRenderingJob> jobs : jobPhases) {
			phases.add(new PipelinePhase(new LinkedList<IRenderingJob>(jobs)));
			
			for (IRenderingJob job : jobs)
				injector.injectMembers(job);
		}
		
		phaseIter = phases.iterator();
		currentPhase = phaseIter.next();
		this.injector = injector;
	}

	@Override
	public void runProcess(final IProcessObserver processObserver) {
		final IRenderingJob nextJob;
		
		synchronized(phaseIter) {
			nextJob = currentPhase.jobs.poll();
		}
		
		if (nextJob != null) {
			try {
				nextJob.run(this);
			} catch(Throwable e) {
				log.error("Rendering process job '" + nextJob + "' failed", e);
				processObserver.terminateProcess();
				observer.finishedWithError(e);
				return;
			}
			
			synchronized(phaseIter) {
				if (--currentPhase.pendingSize == 0) {
					if (phaseIter.hasNext()) {
						currentPhase = phaseIter.next();
					} else {
						log.info("Finished " + this + " in " + (System.currentTimeMillis() - startTime) + "ms");
						processObserver.terminateProcess();
						observer.finished();
					}
				}
			}
		}
	}
	
	@Override
	public void execute(final IRenderingJob job) {
		injector.injectMembers(job);
		
		synchronized(phaseIter) {
			currentPhase.jobs.add(job);
			currentPhase.pendingSize++;
		}
	}

	@Override
	public IBundle getBundle() {
		return context.getBundle();
	}

	@Override
	public URI getResourcePrefix() {
		return context.getResourcePrefix();
	}

	@Override
	public String getProperty(String name) {
		return context.getProperty(name);
	}

	@Override
	public void setProperty(String name, String value) {
		context.setProperty(name, value);
	}

	@Override
	public TransformerHandler createTransformerHandler(Templates templates, URI outputUri)
			throws IOException, TransformerConfigurationException {
		return context.createTransformerHandler(templates, outputUri);
	}

	@Override
	public Templates compileTemplates(Collection<URI> xslSourceUris) throws TransformerConfigurationException {
		return context.compileTemplates(xslSourceUris);
	}
	
	@Override
	public Templates compileTemplates(URI xslSourceUri) throws TransformerConfigurationException, ResourceNotFoundException {
		return context.compileTemplates(xslSourceUri);
	}

	@Override
	public String toString() {
		return "RenderingProcess [" + context.getBundle().getName() + ']';
	}

	@Override
	public void parse(URI publicUri, ContentHandler handler)
			throws IOException, SAXException, ParserConfigurationException, ResourceNotFoundException {
		context.parse(publicUri, handler);
	}

	@Override
	public XMLReader createXMLReader() throws SAXException {
		return context.createXMLReader();
	}

	@Override
	public ContentHandler createXMLWriter(URI publicUri)
			throws IOException, TransformerConfigurationException {
		return context.createXMLWriter(publicUri);
	}

	@Override
	public XMLFilter createXMLFilter(Templates templates, XMLReader parent)
			throws TransformerConfigurationException {
		return context.createXMLFilter(templates, parent);
	}

	@Override
	public Path unzip(URI uri) throws ResourceNotFoundException, IOException {
		return context.unzip(uri);
	}

	@Override
	public Source createXmlSource(URI uri) throws ResourceNotFoundException, IOException {
		return context.createXmlSource(uri);
	}

	@Override
	public Path resolveSource(URI publicUri) throws ResourceNotFoundException {
		return context.resolveSource(publicUri);
	}

	@Override
	public Path resolveDestination(URI publicUri) {
		return context.resolveDestination(publicUri);
	}

	@Override
	public XMLEventReader createXMLEventReader(InputStream stream)
			throws XMLStreamException {
		return context.createXMLEventReader(stream);
	}

	@Override
	public Marshaller createMarshaller() throws JAXBException {
		return context.createMarshaller();
	}

	@Override
	public Path getTempDirectory() {
		return context.getTempDirectory();
	}

	@Override
	public IMetadata extractMetadata(URI uri) throws ResourceNotFoundException,
			MetadataExtractionException {
		return context.extractMetadata(uri);
	}
}
