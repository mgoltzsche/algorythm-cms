package de.algorythm.cms.common.rendering.pipeline.job;

import java.io.File;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import de.algorythm.cms.common.model.entity.IPage;
import de.algorythm.cms.common.model.loader.IBundleLoader;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class PageIndexer implements IRenderingJob {

	@Inject
	private IBundleLoader pageLoader;
	@Inject
	private JAXBContext jaxbContext;
	
	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final File outputDirectory = ctx.getTempDirectory();
		final IPage startPage = pageLoader.loadPages(ctx.getBundle());
		final Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(startPage, new File(outputDirectory, "pages.xml"));
	}

	@Override
	public int hashCode() {
		return 31 * 1 + getClass().getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() == obj.getClass())
			return true;
		return false;
	}
}
