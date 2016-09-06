<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	exclude-result-prefixes="c">

	<xsl:template match="*">
		<div class="error missing-template">
			<xsl:text>ERROR: Missing template for &lt;</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:for-each select="@*">
				<xsl:value-of select="concat(' ', name(), '=', '&quot;', ., '&quot;')"/>
			</xsl:for-each>
			<xsl:choose>
				<xsl:when test="*">
					<xsl:text>&gt;…&lt;/</xsl:text>
					<xsl:value-of select="name()" />
					<xsl:text>&gt;</xsl:text>
				</xsl:when>
				<xsl:when test="text()">
					<xsl:text>&gt;</xsl:text>
					<xsl:value-of select="text()" />
					<xsl:text>&lt;/</xsl:text>
					<xsl:value-of select="name()" />
					<xsl:text>&gt;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>/&gt;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>
