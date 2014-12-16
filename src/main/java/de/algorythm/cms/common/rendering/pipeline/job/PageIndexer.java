package de.algorythm.cms.common.rendering.pipeline.job;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;

import org.w3c.dom.Document;

import com.google.common.base.Joiner;

import de.algorythm.cms.common.model.entity.IBundle;
import de.algorythm.cms.common.model.entity.ISupportedLocale;
import de.algorythm.cms.common.model.loader.impl.PageVisitor;
import de.algorythm.cms.common.model.loader.impl.PageVisitor.IPageVisitor;
import de.algorythm.cms.common.rendering.pipeline.IBundleRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class PageIndexer implements IRenderingJob {
	
	private final List<URI> templates = new LinkedList<URI>();
	private URI notFoundContent;
	
	@Override
	public void run(final IRenderingContext ctx) throws Exception {
		final IBundle bundle = ctx.getBundle();
		final Path location = bundle.getLocation();
		final Path internationalDir = location.resolve("international");
		final Path pagesDir = internationalDir.resolve("pages");
		final Templates compiledTemplates = ctx.compileTemplates(templates);
		final Set<ISupportedLocale> supportedLocales = bundle.getSupportedLocales();
		final boolean localizeOutput = supportedLocales.size() > 1;
		
		for (ISupportedLocale supportedLocale : supportedLocales) {
			final Locale locale = supportedLocale.getLocale();
			final IBundleRenderingContext localizedCtx = ctx.createLocalized(locale, localizeOutput);
			
			Files.walkFileTree(pagesDir, new PageVisitor(new IPageVisitor() {
				@Override
				public void visit(final Path file) {
					final String uriStr = '/' + internationalDir.relativize(file).toString();
					final URI uri = URI.create(uriStr);
					
					ctx.execute(new IRenderingJob() {
						@Override
						public void run(IRenderingContext c) throws Exception {
							final Transformer transformer = localizedCtx.createTransformer(compiledTemplates, notFoundContent);
							
							localizedCtx.transform(uri, URI.create("tmp://" + uriStr), transformer);
							Document document = localizedCtx.getDocument(uri);
							//System.out.println(document.getDocumentElement().cloneNode(false));
						}
					});
				}
			}));
			//System.out.println(Joiner.on("\n").join(Files.readAllLines(localizedCtx.getTempDirectory().resolve("en/pages/page.xml"), StandardCharsets.UTF_8)));
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((notFoundContent == null) ? 0 : notFoundContent.hashCode());
		result = prime * result
				+ ((templates == null) ? 0 : templates.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageIndexer other = (PageIndexer) obj;
		if (notFoundContent == null) {
			if (other.notFoundContent != null)
				return false;
		} else if (!notFoundContent.equals(other.notFoundContent))
			return false;
		if (templates == null) {
			if (other.templates != null)
				return false;
		} else if (!templates.equals(other.templates))
			return false;
		return true;
	}
}
