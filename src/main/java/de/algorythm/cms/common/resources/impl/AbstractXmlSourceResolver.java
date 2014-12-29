package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
	private final SynchronizedContext<URI, Void> synchronizer = new SynchronizedContext<URI, Void>();
	
	public AbstractXmlSourceResolver(final IMetadataExtractor metadataExtractor) {
		this.metadataExtractor = metadataExtractor;
	}
	
	@Override
	public final Source createXmlSource(URI uri, final IBundleRenderingContext ctx)
			throws ResourceNotFoundException, IOException {
		uri = uri.normalize();
		
		if ("metadata".equals(uri.getScheme())) {
			final Path metadataFile = ctx.resolveDestination(URI.create("tmp:///meta" + uri.getPath()));
			
			synchronizer.synchronize(uri, new Function<URI, Void>() {
				@Override
				public Void apply(URI uri) {
					if (!Files.exists(metadataFile)) {
						try {
							final IMetadata metadata;
							
							try {
								metadata = metadataExtractor.extractMetadata(uri, ctx);
							} catch (ResourceNotFoundException e) {
								throw new RuntimeException(e);
							}
							
							final Marshaller marshaller = ctx.createMarshaller();
							
							try (Writer writer = Files.newBufferedWriter(metadataFile, StandardCharsets.UTF_8)) {
								marshaller.marshal(metadata, new StreamResult(writer));
							}
						} catch(MetadataExtractionException |JAXBException | IOException e) {
							throw new RuntimeException(e);
						}
					}
					return null;
				}
			});
			
			try {
				return new XmlSource(uri, metadataFile);
			} catch (IOException e) {
				throw new RuntimeException("Cannot extract metadata of " + uri, e);
			}
		} else {
			return createXmlSourceInternal(uri, ctx);
		}
	}
	
	protected abstract Source createXmlSourceInternal(final URI uri, final IBundleRenderingContext ctx) throws ResourceNotFoundException, IOException;
}
