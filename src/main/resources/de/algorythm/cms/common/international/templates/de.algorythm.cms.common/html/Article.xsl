<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	exclude-result-prefixes="c">
	
	<xsl:template match="c:article">
		<xsl:param name="document-root" select="false()"/>
		<article>
			<xsl:if test="@title and $document-root = false()">
				<h2>
					<xsl:value-of select="@title" />
				</h2>
			</xsl:if>
			<xsl:apply-templates />
		</article>
	</xsl:template>
</xsl:stylesheet>
