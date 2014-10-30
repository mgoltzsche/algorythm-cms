package de.algorythm.cms.common.impl.xml.contentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.algorythm.cms.common.impl.xml.Constants.Namespace;
import de.algorythm.cms.common.impl.xml.Constants.Tag;
import de.algorythm.cms.common.impl.xml.InformationCompleteException;
import de.algorythm.cms.common.model.entity.impl.PageInfo;

public class PageInfoHandler extends DefaultHandler {

	static private interface IHandlerStrategy {
		void startElement(PageInfoHandler handler, String uri, String localName, Attributes atts) throws SAXException;
	}
	
	static private final IHandlerStrategy rootElementStrategy = new IHandlerStrategy() {
		@Override
		public void startElement(final PageInfoHandler handler, final String uri,
				final String localName, final Attributes atts) throws SAXException {
			handler.startRootElement(uri, localName, atts);
			handler.strategy = elementStrategy;
		}
	};
	
	static private final IHandlerStrategy elementStrategy = new IHandlerStrategy() {
		@Override
		public void startElement(final PageInfoHandler handler, final String uri,
				final String localName, final Attributes atts) throws SAXException {
			handler.startElement(uri, localName, atts);
		}
	};
	
	private final String rootTag;
	private IHandlerStrategy strategy;
	private PageInfo pageInfo;

	public PageInfoHandler() {
		rootTag = Tag.PAGE;
	}
	
	public PageInfoHandler(final String rootTag) {
		this.rootTag = rootTag;
	}
	
	public void setPage(final PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	@Override
	public void startDocument() throws SAXException {
		strategy = rootElementStrategy;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes atts) throws SAXException {
		strategy.startElement(this, uri, localName, atts);
	}
	
	protected void startRootElement(final String uri,
			final String localName, final Attributes atts) throws SAXException {
		if (!Namespace.CMS.equals(uri))
			throw new SAXException("Unexpected root tag namespace '" + uri + "'. Expecting '" + Namespace.CMS + "'");
		
		if (!rootTag.equals(localName))
			throw new SAXException("Unexpected root tag '" + localName + "'. Expecting '" + rootTag + "'");
		
		final String title = atts.getValue("title");
		
		pageInfo.setNavigationTitle(atts.getValue("nav-title"));
		pageInfo.setInNavigation(atts.getValue("in-menu").equals("true"));
		
		if (title != null && !title.trim().isEmpty()) {
			pageInfo.setTitle(title);
			finishedParsing();
			throw new InformationCompleteException();
		}
	}
	
	protected void startElement(final String uri,
			final String localName, final Attributes atts) throws SAXException {
		final String title = atts.getValue("title");
		final String navTitle = atts.getValue("nav-title");
		
		if (navTitle != null && !navTitle.trim().isEmpty() &&
				pageInfo.getNavigationTitle() == null)
			pageInfo.setNavigationTitle(navTitle);
		
		if (title != null && !title.trim().isEmpty()) {
			pageInfo.setTitle(title);
			
			finishedParsing();
			throw new InformationCompleteException();
		}
	}

	@Override
	public void endDocument() throws SAXException {
		finishedParsing();
	}

	private void finishedParsing() {
		final String title = pageInfo.getTitle();
		final String navTitle = pageInfo.getNavigationTitle();
		
		if (title == null || title.trim().isEmpty())
			pageInfo.setTitle(pageInfo.getName());
		
		if (navTitle == null || navTitle.trim().isEmpty())
			pageInfo.setNavigationTitle(pageInfo.getTitle());
	}
}
