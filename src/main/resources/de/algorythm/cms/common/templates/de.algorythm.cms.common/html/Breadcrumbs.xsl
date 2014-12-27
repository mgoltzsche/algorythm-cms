<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	exclude-result-prefixes="c p">
	<xsl:param name="relativeBaseUrl" />
	<xsl:param name="page.path" />
	
	<xsl:template match="c:breadcrumbs">
		<xsl:variable name="current" select="$c:pages//*[@path=$page.path]" />
		<xsl:if test="$current != $c:pages/p:page">
			<nav class="breadcrumbs">
				<ul>
					<xsl:call-template name="c:breadcrumbs">
						<xsl:with-param name="current" select="if (@disable-last = true()) then $current/.. else $current" />
					</xsl:call-template>
				</ul>
			</nav>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="c:breadcrumbs">
		<xsl:param name="current" />
		<xsl:if test="$current/../..">
			<xsl:call-template name="c:breadcrumbs">
				<xsl:with-param name="current" select="$current/.." />
			</xsl:call-template>
		</xsl:if>
		<li>
			<xsl:call-template name="c:link">
				<xsl:with-param name="page" select="$current" />
			</xsl:call-template>
			<xsl:value-of select="' » '" />
		</li>
	</xsl:template>
</xsl:stylesheet>