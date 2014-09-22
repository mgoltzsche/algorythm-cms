package de.algorythm.cms.common;

import org.junit.Test;

public class TestMain {

	@Test
	public void testMain() {
		new CmsCommonMain(new CmsCommonModule()).generateAll();
	}
}
