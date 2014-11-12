<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c">
	<xsl:param name="pagePath" />
	
	<xsl:template match="c:nav">
		<nav class="pure-menu pure-menu-open pure-menu-vertical">
			<ul>
				<xsl:apply-templates select="document('/pages.xml')//*[@path=$pagePath]/*"/>
			</ul>
		</nav>
	</xsl:template>
</xsl:stylesheet>
