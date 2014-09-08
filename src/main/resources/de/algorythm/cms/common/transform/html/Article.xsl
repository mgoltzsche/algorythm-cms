<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:a="http://cms.algorythm.de/common/Article"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="a">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="yes" indent="yes" />

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
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:include href="Markup.xsl" />
</xsl:stylesheet>
