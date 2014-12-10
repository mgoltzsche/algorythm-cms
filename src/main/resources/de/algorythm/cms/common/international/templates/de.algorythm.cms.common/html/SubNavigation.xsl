<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c">
	<xsl:param name="page.path" />
	
	<xsl:template match="c:nav">
		<xsl:variable name="baseUri" select="concat('page:', $page.path, '/')" />
		<xsl:variable name="parent" select="if (ends-with(./@parent, '/')) then ./@parent else concat(./@parent, '/')" />
		<xsl:variable name="parentPath" select="resolve-uri($parent, $baseUri)" />
		<nav class="pure-menu pure-menu-open pure-menu-vertical">
			<xsl:call-template name="c:menu">
				<xsl:with-param name="root" select="document('/pages.xml')//*[concat('page:', @path, '/')=$parentPath]" />
				<xsl:with-param name="maxDepth" select="if (./@depth) then number(./@depth) else 0" />
			</xsl:call-template>
		</nav>
	</xsl:template>
</xsl:stylesheet>
