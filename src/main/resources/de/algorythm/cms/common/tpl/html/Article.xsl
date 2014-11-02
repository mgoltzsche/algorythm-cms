<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:a="http://cms.algorythm.de/common/Article"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="a">
	<xsl:template match="a:article">
		<article>
			<xsl:if test="@title">
				<h3>
					<xsl:value-of select="@title" />
				</h3>
			</xsl:if>
			<xsl:apply-templates />
		</article>
	</xsl:template>
</xsl:stylesheet>
