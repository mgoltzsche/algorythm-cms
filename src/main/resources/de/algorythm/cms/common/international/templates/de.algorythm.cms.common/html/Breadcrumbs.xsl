<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	exclude-result-prefixes="c p">
	<xsl:param name="page.path" />
	
	<xsl:template match="c:breadcrumbs">
		<xsl:variable name="pages" select="document('/pages.xml')" />
		<xsl:variable name="current" select="$pages//*[@path=$page.path]" />
		<xsl:variable name="root" select="if (boolean(@ignore-root) = true()) then $pages/p:page else ''" />
		<xsl:value-of select="boolean(@ignore-root)" />
		<xsl:if test="$current/.. != $root">
			<ul>
				<xsl:call-template name="c:breadcrumbs">
					<xsl:with-param name="current" select="$current" />
					<xsl:with-param name="root" select="$root" />
				</xsl:call-template>
			</ul>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="c:breadcrumbs">
		<xsl:param name="current" />
		<xsl:param name="root" />
		<xsl:if test="$current/.. != $root">
			<xsl:call-template name="c:breadcrumbs">
				<xsl:with-param name="current" select="$current/.." />
				<xsl:with-param name="root" select="$root" />
			</xsl:call-template>
		</xsl:if>
		<li>
			<xsl:value-of select="$current/@nav-title" />
		</li>
	</xsl:template>
</xsl:stylesheet>