<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns="http://www.w3.org/2000/svg"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<svg id="icon-sprite"
				version="1.1">
			<xsl:for-each select="*/*">
				<xsl:variable name="svg" select="document(@uri)" />
				<xsl:variable name="fileName" select="tokenize(@uri,'/')[last()]" />
				<xsl:variable name="id" select="$svg/*/@id" />
				<xsl:variable name="id" select="if ($id) then $id else substring($fileName, 0, string-length($fileName) - 4)" />
				<symbol id="{$id}" viewBox="{$svg/*/@viewBox}">
					<xsl:copy-of select="$svg/*/*" />
				</symbol>
			</xsl:for-each>
		</svg>
	</xsl:template>
</xsl:stylesheet>