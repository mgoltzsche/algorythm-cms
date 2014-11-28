package de.algorythm.cms.common.rendering.pipeline;

public interface IRenderingContext extends IBundleRenderingContext {

	void execute(IRenderingJob job);
}
