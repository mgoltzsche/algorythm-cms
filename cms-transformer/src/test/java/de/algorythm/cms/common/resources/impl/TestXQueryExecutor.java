package de.algorythm.cms.common.resources.impl;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.algorythm.cms.common.rendering.pipeline.job.XQueryPageIndexer;
import de.algorythm.cms.common.resources.IInputResolver;

public class TestXQueryExecutor {

	@Test
	public void executeXQuery_should_modify_document() throws Exception {
		URI docUri = URI.create("/de/algorythm/cms/common/contents/about.xml");
		URI xqueryUri = URI.create("/de/algorythm/cms/common/xquery/modify.xql");
		URI repoUri = getClass().getResource("/integration-test-repo/").toURI();
		Path repoDir = Paths.get(repoUri);
		IInputResolver resolver = new FileInputSourceResolver(repoDir, new ClasspathInputSourceResolver());
		XQueryPageIndexer testee = new XQueryPageIndexer();
		
		testee.executeXQuery(docUri, xqueryUri, resolver, null);
	}
}
