package de.algorythm.cms.common.impl.xml.contentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.algorythm.cms.common.impl.xml.InformationCompleteException;
import de.algorythm.cms.common.model.dao.impl.xml.XmlResourceDao;
import de.algorythm.cms.common.model.entity.impl.Page;
import static de.algorythm.cms.common.impl.xml.Constants.*;

public class PageInfoHandler extends DefaultHandler {

	private boolean validPage;
	private String title;
	private String navTitle;
	private boolean inMenu;

	public Page createPageInfo(final XmlResourceDao dao, final String site, final String parentPath, final String name) throws SAXException {
		if (!validPage)
			throw new SAXException("Invalid page XML. Expected namespace " + Namespace.CMS);
		
		if (title == null || title.isEmpty())
			title = name;
		
		if (navTitle == null || navTitle.trim().isEmpty())
			navTitle = title;
		
		return new Page(dao, site, parentPath, name, title, navTitle, inMenu);
	}
	
	@Override
	public void startDocument() throws SAXException {
		validPage = false;
		title = navTitle = null;
		inMenu = true;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes atts) throws SAXException {
		if (Namespace.CMS.equals(uri) && Tag.PAGE.equals(localName)) {
			validPage = true;
			title = atts.getValue("title");
			navTitle = atts.getValue("nav-title");
			inMenu = atts.getValue("in-menu").equals("true");
			
			/*for (int i = 0; i < atts.getLength(); i++) {
				final String attrName = atts.getQName(i);
				
				if (attrName.equals("title"))
					title = atts.getValue(i);
				else if (attrName.equals("nav-title"))
					navTitle = atts.getValue(i);
				else if (attrName.equals("in-menu"))
					inMenu = atts.getValue(i).equals("true");
			}*/
			
			if (title != null && !title.isEmpty())
				throw new InformationCompleteException();
		} else {
			final String contentTitle = atts.getValue("title");
			
			if (contentTitle != null) {
				title = contentTitle;
				final String contentNavTitle = atts.getValue("nav-title");
				
				if (contentNavTitle != null && navTitle == null)
					navTitle = contentNavTitle;
				
				throw new InformationCompleteException();
			}
		}
	}
}
