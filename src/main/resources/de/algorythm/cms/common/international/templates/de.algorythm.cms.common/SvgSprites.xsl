<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns="http://www.w3.org/2000/svg"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS">
	<xsl:template match="/">
		<svg id="icon-sprite" version="1.1">
			<xsl:for-each select="*/*">
				<xsl:variable name="svg" select="document(@uri)" />
				<xsl:variable name="fileName" select="tokenize(@uri,'/')[last()]" />
				<xsl:variable name="id" select="$svg/*/@id" />
				<xsl:variable name="id" select="substring($fileName, 0, string-length($fileName) - 3)" />
				<xsl:variable name="viewBox" select="if ($svg/*/@viewBox) then $svg/*/@viewBox else concat('0 0 ', c:strip-unit($svg/*/@width), ' ', c:strip-unit($svg/*/@height))" />
				<symbol id="{$id}" viewBox="{$viewBox}">
					<xsl:copy-of select="$svg/*/*" />
				</symbol>
			</xsl:for-each>
		</svg>
	</xsl:template>
	
	<xsl:function name="c:strip-unit">
		<xsl:param name="value" />
		<xsl:analyze-string select="$value"
				regex="^([\d]+)([a-z]+)?$">
			<xsl:matching-substring>
				<xsl:value-of select="regex-group(1)" />
			</xsl:matching-substring>
		</xsl:analyze-string>
	</xsl:function>
</xsl:stylesheet>