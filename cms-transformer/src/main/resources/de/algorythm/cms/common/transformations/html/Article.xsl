<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	exclude-result-prefixes="c">
	
	<xsl:template match="c:article">
		<article>
			<xsl:apply-templates />
		</article>
	</xsl:template>
</xsl:stylesheet>
