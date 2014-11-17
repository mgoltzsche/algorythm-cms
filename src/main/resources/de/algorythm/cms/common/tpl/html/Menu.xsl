<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="p">
	
	<xsl:template name="menu" match="p:page">
		<xsl:param name="depth" select="1" />
		<xsl:param name="maxDepth" select="0" />
		<xsl:if test="boolean(current()/@in-navigation)=true()">
			<li>
				<a href="{$relativeBaseUrl}{current()/@path}/index.html"><xsl:value-of select="current()/@title" /></a>
				<xsl:if test="./* and ($maxDepth lt 1 or $depth lt $maxDepth)">
					<ul>
						<xsl:apply-templates>
							<xsl:with-param name="depth" select="$depth + 1" />
							<xsl:with-param name="maxDepth" select="$maxDepth" />
						</xsl:apply-templates>
					</ul>
				</xsl:if>
			</li>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
