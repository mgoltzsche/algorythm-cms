<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:a="http://cms.algorythm.de/common/Article"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="a">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="yes" indent="yes" />
	<xsl:import href="Markup.xsl" />
	
	<xsl:template match="/">
		<article>
			<xsl:if test="/a:article/@title">
				<h3>
					<xsl:value-of select="/a:article/@title" />
				</h3>
			</xsl:if>
			<xsl:apply-imports />
		</article>
	</xsl:template>
</xsl:stylesheet>
