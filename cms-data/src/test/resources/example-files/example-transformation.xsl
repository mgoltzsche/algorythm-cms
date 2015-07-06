<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="*">
		<article>
			<xsl:value-of select="concat(@*, '. ')" />
			<xsl:value-of select="." />
		</article>
	</xsl:template>
</xsl:stylesheet>