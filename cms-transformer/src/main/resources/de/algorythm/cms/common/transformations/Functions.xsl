<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS">

	<xsl:function name="c:path">
		<xsl:param name="node" />
		<xsl:for-each select="$node/ancestor-or-self::*">
			<xsl:text disable-output-escaping="yes">/*</xsl:text>
			<xsl:text disable-output-escaping="yes">[</xsl:text>
			<xsl:value-of select="count(preceding-sibling::*)" />
			<xsl:text disable-output-escaping="yes">]</xsl:text>
		</xsl:for-each>
	</xsl:function>
</xsl:stylesheet>
