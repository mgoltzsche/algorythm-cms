<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:m="http://www.algorythm.de/cms/Markup">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="yes" indent="yes" />
	<xsl:param name="versionParam" select="'1.0'" />

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="m:b">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>
</xsl:stylesheet>
