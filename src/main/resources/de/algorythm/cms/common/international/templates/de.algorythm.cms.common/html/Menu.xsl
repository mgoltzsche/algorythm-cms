<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="p">
	<xsl:param name="page.path" />
	
	<xsl:template name="c:menu">
		<xsl:param name="root" select="document('/pages.xml')/p:page" />
		<xsl:param name="depth" select="1" />
		<xsl:param name="maxDepth" select="0" />
		<ul>
			<xsl:for-each select="$root/p:page">
				<xsl:if test="boolean(@in-navigation)=true()">
					<li class="{if (@path = $page.path or starts-with($page.path, concat(@path, '/'))) then 'pure-menu-selected' else ''}">
						<a href="{$relativeBaseURL}{@path}/index.html"><xsl:value-of select="current()/@title" /></a>
						<xsl:if test="* and ($maxDepth lt 1 or $depth lt $maxDepth)">
							<xsl:call-template name="c:menu">
								<xsl:with-param name="root" select="p:page" />
								<xsl:with-param name="depth" select="$depth + 1" />
								<xsl:with-param name="maxDepth" select="$maxDepth" />
							</xsl:call-template>
						</xsl:if>
					</li>
				</xsl:if>
			</xsl:for-each>
		</ul>
		
	</xsl:template>
</xsl:stylesheet>
