<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:m="http://cms.algorythm.de/common/Markup"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="m">

	<xsl:template match="m:p">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>
	
	<xsl:template match="m:b">
		<b>
			<xsl:apply-templates />
		</b>
	</xsl:template>
	
	<xsl:template match="m:i">
		<i>
			<xsl:apply-templates />
		</i>
	</xsl:template>
</xsl:stylesheet>
