<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:a="http://www.algorythm.de/cms/Article"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="yes" indent="yes" />
	<xsl:param name="versionParam" select="'1.0'" />

	<xsl:import href="markup.xsl"/>

	<xsl:template match="/">
		<article>
			<xsl:apply-templates />
		</article>
	</xsl:template>

	<xsl:template match="a:title">
		<h3>
			<xsl:apply-templates />
		</h3>
	</xsl:template>

	<xsl:template match="a:content">
		<div>
			<xsl:apply-imports />
		</div>
	</xsl:template>
</xsl:stylesheet>
