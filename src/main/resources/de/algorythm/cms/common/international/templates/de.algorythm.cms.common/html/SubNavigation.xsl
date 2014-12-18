<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	exclude-result-prefixes="c p">
	<xsl:param name="page.path" />
	
	<xsl:template match="c:nav">
		<xsl:variable name="absoluteRootPath" select="c:absolute-path(@parent)" />
		
		<nav class="pure-menu pure-menu-open pure-menu-vertical">
			<xsl:call-template name="c:menu-html">
				<xsl:with-param name="root" select="$c:pages//*[@path=$absoluteRootPath]" />
				<xsl:with-param name="maxDepth" select="if (@depth) then number(@depth) else 0" />
			</xsl:call-template>
		</nav>
	</xsl:template>
</xsl:stylesheet>
