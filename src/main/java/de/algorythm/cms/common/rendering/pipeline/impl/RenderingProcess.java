package de.algorythm.cms.common.rendering.pipeline.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

import com.google.inject.Injector;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;
import de.algorythm.cms.common.rendering.pipeline.IXmlLoader;
import de.algorythm.cms.common.resources.IOutputUriResolver;
import de.algorythm.cms.common.resources.IUriResolver;
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
	private final long startTime = new Date().getTime();

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
						log.info("Finished " + this + " in " + (new Date().getTime() - startTime) + "ms");
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
	public Path getTempDirectory() {
		return context.getTempDirectory();
	}

	@Override
	public Path getOutputDirectory() {
		return context.getOutputDirectory();
	}

	@Override
	public IUriResolver getResourceResolver() {
		return context.getResourceResolver();
	}
	
	@Override
	public IOutputUriResolver getOutputResolver() {
		return context.getOutputResolver();
	}

	@Override
	public XMLReader createXmlReader() {
		return context.createXmlReader();
	}
	
	@Override
	public IXmlLoader getXmlLoader() {
		return context.getXmlLoader();
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
	public IBundleRenderingContext createLocalized(Locale locale,
			boolean localizeOutput) {
		return context.createLocalized(locale, localizeOutput);
	}

	@Override
	public Document getDocument(URI uri) {
		return context.getDocument(uri);
	}

	@Override
	public void transform(URI sourceUri, URI targetUri, Transformer transformer)
			throws IOException, TransformerException {
		context.transform(sourceUri, targetUri, transformer);
	}

	@Override
	public Transformer createTransformer(Templates templates,
			URI notFoundContent) throws TransformerConfigurationException {
		return context.createTransformer(templates, notFoundContent);
	}

	@Override
	public Templates compileTemplates(Collection<URI> xslSourceUris) {
		return context.compileTemplates(xslSourceUris);
	}
	
	@Override
	public String toString() {
		return "RenderingProcess [" + context.getBundle().getName() + ']';
	}
}
