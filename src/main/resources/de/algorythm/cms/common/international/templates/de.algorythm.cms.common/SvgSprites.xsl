<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns="http://www.w3.org/2000/svg"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:s="http://cms.algorythm.de/common/Sources">
	<xsl:template match="/">
		<svg xmlns="http://www.w3.org/2000/svg" id="Layer_1"
				x="0px" y="0px"
				width="100px" height="100px"
				viewBox="0 0 100 100" enable-background="new 0 0 100 100" xml:space="preserve"
				version="1.1">
			<defs>
				<xsl:for-each select="*/*">
					<xsl:variable name="svg" select="document(@uri)" />
					<xsl:variable name="fileName" select="tokenize(@uri,'/')[last()]" />
					<xsl:variable name="fileName" select="substring($fileName, 0, string-length($fileName) - 4)" />
					<view id="{$svg/*/@id}" viewBox="{$svg/*/@viewBox}">
						<xsl:copy-of select="$svg/*/*" />
					</view>
				</xsl:for-each>
			</defs>
		</svg>
	</xsl:template>
</xsl:stylesheet>
