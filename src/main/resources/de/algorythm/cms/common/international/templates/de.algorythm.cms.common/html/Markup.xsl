<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	exclude-result-prefixes="c">

	<xsl:template match="c:p">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>
	
	<xsl:template match="c:b">
		<b>
			<xsl:apply-templates />
		</b>
	</xsl:template>
	
	<xsl:template match="c:i">
		<i>
			<xsl:apply-templates />
		</i>
	</xsl:template>
</xsl:stylesheet>
