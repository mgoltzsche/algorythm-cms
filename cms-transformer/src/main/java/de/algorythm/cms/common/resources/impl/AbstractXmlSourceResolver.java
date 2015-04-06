package de.algorythm.cms.common.resources.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
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
import de.algorythm.cms.common.resources.IWriteableResources;
import de.algorythm.cms.common.resources.IXmlSourceResolver;
import de.algorythm.cms.common.resources.ResourceNotFoundException;
import de.algorythm.cms.common.resources.meta.IMetadataExtractor;
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
					final IWriteableResources meta = ctx.getMetaResources();
					final Path metaFile = meta.resolvePublicPath(uri.getPath());
					
					if (!Files.exists(metaFile)) {
						try {
							final IMetadata metadata = metadataExtractor.extractMetadata(uri, ctx, ctx.getTmpResources());
							final Marshaller marshaller = xmlFactory.createMarshaller();
							
							Files.createDirectories(metaFile.getParent());
							
							try (OutputStream out = Files.newOutputStream(metaFile)) {
								marshaller.marshal(metadata, new StreamResult(out));
							}
						} catch (ResourceNotFoundException | IOException | JAXBException e) {
							throw new RuntimeException("Cannot extract metadata", e);
						}
					}
					
					try {
						return new XmlSource(uri, metaFile);
					} catch (IOException e) {
						throw new RuntimeException("Cannot read metadata of " + uri, e);
					}
				}
			});
		} else {
			return createXmlSourceInternal(uri, ctx);
		}
	}

	protected abstract Source createXmlSourceInternal(final URI uri, final IRenderingContext ctx) throws ResourceNotFoundException, IOException;
}
