<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:functx="http://www.functx.com"
	exclude-result-prefixes="c">
	
	<xsl:template match="c:article">
		<article edit-rich-text="" contenteditable="false">
			<xsl:value-of select="c:path(@title)" /><br/>
			<xsl:value-of select="c:dynamic-path(/, substring-after(fn:string-join(c:path(@title), ''), '/'))" /><br/>
			<xsl:value-of select="c:dynamic-path(/c:page, '*[3]/@src')" />
			<xsl:apply-templates />
		</article>
	</xsl:template>
</xsl:stylesheet>
