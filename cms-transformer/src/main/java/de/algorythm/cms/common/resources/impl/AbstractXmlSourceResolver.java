package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;

import com.google.common.base.Function;

import de.algorythm.cms.common.model.entity.IMetadata;
import de.algorythm.cms.common.rendering.pipeline.IMetadataExtractorProvider;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IXmlFactory;
import de.algorythm.cms.common.resources.IOutputTarget;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;
import de.algorythm.cms.common.scheduling.impl.SynchronizedContext;

public abstract class AbstractXmlSourceResolver implements IXmlSourceResolver {

	private final IXmlFactory xmlFactory;
	private final IMetadataExtractor metadataExtractor;
	private final SynchronizedContext<URI, Source> synchronizer = new SynchronizedContext<URI, Source>();
	
	public AbstractXmlSourceResolver(final IXmlFactory xmlFactory, final IMetadataExtractorProvider metadataExtractorProvider) {
		this.xmlFactory = xmlFactory;
		this.metadataExtractor = metadataExtractorProvider.getMetadataExtractor();
	}
	
	@Override
	public final Source createXmlSource(URI uri, final IRenderingContext ctx)
			throws ResourceNotFoundException, IOException {
		final String scheme = uri.getScheme();
		uri = uri.normalize();
		
		if ("metadata".equals(scheme)) {
			return synchronizer.synchronize(uri, new Function<URI, Source>() {
				@Override
				public Source apply(URI uri) {
					final URI metadataUri = URI.create("/meta" + uri.getPath());
					Path metadataFile;
					
					try {
						metadataFile = ctx.resolveSource(metadataUri);
					} catch(ResourceNotFoundException ex) {
						try {
							final IMetadata metadata;
							
							try {
								metadata = metadataExtractor.extractMetadata(uri, ctx);
							} catch (ResourceNotFoundException e) {
								throw new RuntimeException(e);
							}
							
							final Marshaller marshaller = xmlFactory.createMarshaller();
							final IOutputTarget target = ctx.createOutputTarget("/meta" + uri.getPath());
							
							try (OutputStream out = target.createOutputStream()) {
								marshaller.marshal(metadata, new StreamResult(out));
							}
						} catch(MetadataExtractionException |JAXBException | IOException e) {
							throw new RuntimeException(e);
						}
						
						try {
							metadataFile = ctx.resolveSource(metadataUri);
						} catch (ResourceNotFoundException e) {
							throw new RuntimeException(e);
						}
					}
					
					try {
						return new XmlSource(uri, metadataFile);
					} catch (IOException e) {
						throw new RuntimeException("Cannot extract metadata of " + uri, e);
					}
				}
			});
		} else {
			return createXmlSourceInternal(uri, ctx);
		}
	}
	
	protected abstract Source createXmlSourceInternal(final URI uri, final IRenderingContext ctx) throws ResourceNotFoundException, IOException;
}
