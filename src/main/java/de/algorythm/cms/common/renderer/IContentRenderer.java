package de.algorythm.cms.common.renderer;


public interface IContentRenderer {

	String render(String content, String transformation) throws RendererException;
}
