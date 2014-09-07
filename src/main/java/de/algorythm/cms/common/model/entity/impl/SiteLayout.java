package de.algorythm.cms.common.model.entity.impl;

public class SiteLayout {

	private String uuid;
	private String title;
	private String content;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	/*public String render(final Webpage page) {
		return page.getContent().render(renderer);
	}*/
}
