<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	exclude-result-prefixes="c p">
	<xsl:output method="html"/>
	
	<xsl:template match="c:nav">
		<xsl:variable name="parent" select="if (@parent) then @parent else '.'" />
		<xsl:variable name="absoluteRootPath" select="c:absolute-path($parent)" />
		
		<nav class="menu vertical" role="menubar">
			<xsl:if test="@title">
				<h2><xsl:value-of select="@title" /></h2>
			</xsl:if>
			<xsl:call-template name="c:menu-html">
				<xsl:with-param name="root" select="$c:pages//*[@path=$absoluteRootPath]" />
				<xsl:with-param name="maxDepth" select="if (@depth) then number(@depth) else 1" />
			</xsl:call-template>
		</nav>
	</xsl:template>
</xsl:stylesheet>
