package de.algorythm.cms.common.rendering.pipeline;

import java.nio.file.Path;

import org.w3c.dom.Document;

public interface IXmlLoader {

	Document getDocument(Path path);
}
