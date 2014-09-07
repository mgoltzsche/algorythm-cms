<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:t="http://www.algorythm.de/cms/Article"
	xmlns:m="http://www.algorythm.de/cms/Markup"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="yes" indent="yes" />
	<xsl:param name="versionParam" select="'1.0'" />

	<xsl:template match="/">
		<article>
			<xsl:apply-templates />
		</article>
	</xsl:template>

	<xsl:template match="t:title">
		<h3>
			<xsl:apply-templates />
		</h3>
	</xsl:template>

	<xsl:template match="t:content">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="m:b">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>
</xsl:stylesheet>
