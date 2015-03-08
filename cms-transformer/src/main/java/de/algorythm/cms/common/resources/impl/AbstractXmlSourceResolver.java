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
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
import de.algorythm.cms.common.resources.meta.MetadataExtractionException;
import de.algorythm.cms.common.scheduling.impl.SynchronizedContext;

public abstract class AbstractXmlSourceResolver implements IXmlSourceResolver {

	private final IMetadataExtractor metadataExtractor;
	private final SynchronizedContext<URI, Source> synchronizer = new SynchronizedContext<URI, Source>();
	
	public AbstractXmlSourceResolver(final IMetadataExtractor metadataExtractor) {
		this.metadataExtractor = metadataExtractor;
	}
	
	@Override
	public final Source createXmlSource(URI uri, final IBundleRenderingContext ctx)
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
							
							final Marshaller marshaller = ctx.createMarshaller();
							
							try (OutputStream outputStream = ctx.createOutputStream(URI.create("tmp:///meta" + uri.getPath()))) {
								marshaller.marshal(metadata, new StreamResult(outputStream));
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
	
	protected abstract Source createXmlSourceInternal(final URI uri, final IBundleRenderingContext ctx) throws ResourceNotFoundException, IOException;
}
