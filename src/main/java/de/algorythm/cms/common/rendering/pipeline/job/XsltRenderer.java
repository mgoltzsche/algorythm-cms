package de.algorythm.cms.common.rendering.pipeline.job;

import de.algorythm.cms.common.rendering.pipeline.IRenderingContext;
import de.algorythm.cms.common.rendering.pipeline.IRenderingJob;

public class XsltRenderer implements IRenderingJob {

	private int createChildren = 5;
	
	public XsltRenderer() {}
	
	public XsltRenderer(final int createChildren) {
		this.createChildren = createChildren;
	}
	
	@Override
	public void run(final IRenderingContext context) {
		System.out.println("Render XSLT");
		
		if (createChildren > 0)
			context.execute(new XsltRenderer(createChildren - 1));
	}
}
