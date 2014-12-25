<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
		xmlns="http://www.w3.org/2000/svg"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:c="http://cms.algorythm.de/common/CMS"
		exclude-result-prefixes="c">
	<xsl:template match="/">
		<svg id="icon-sprite" version="1.1">
			<xsl:for-each select="*/*">
				<xsl:variable name="svg" select="document(@uri)/*" />
				<xsl:variable name="fileName" select="tokenize(@uri,'/')[last()]" />
				<xsl:variable name="id" select="substring($fileName, 0, string-length($fileName) - 3)" />
				<xsl:variable name="viewBox" select="if ($svg/@viewBox) then $svg/@viewBox else concat('0 0 ', c:strip-unit($svg/@width), ' ', c:strip-unit($svg/@height))" />
				<symbol id="{$id}" viewBox="{$viewBox}">
					<xsl:for-each select="$svg/*">
						<xsl:if test="local-name() != 'metadata' and (local-name() != 'defs' or *)">
							<xsl:call-template name="c:copy-svg" />
						</xsl:if>
					</xsl:for-each>
				</symbol>
			</xsl:for-each>
		</svg>
	</xsl:template>
	
	<xsl:template name="c:copy-svg">
		<xsl:if test="namespace-uri() = 'http://www.w3.org/2000/svg'">
			<xsl:element name="{local-name()}">
				<xsl:for-each select="@*">
					<xsl:attribute name="{local-name()}" namespace="{namespace-uri()}" select="." />
				</xsl:for-each>
				<xsl:for-each select="*">
					<xsl:call-template name="c:copy-svg" />
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:function name="c:strip-unit">
		<xsl:param name="v" />
		<!-- Using choose instead of regex since it is faster -->
		<xsl:choose>
			<xsl:when test="ends-with($v, 'px')"><xsl:value-of select="substring($v, 0, string-length($v) - 1)" /></xsl:when>
			<xsl:when test="ends-with($v, 'pt')"><xsl:value-of select="substring($v, 0, string-length($v) - 1)" /></xsl:when>
			<xsl:when test="ends-with($v, '%')"><xsl:value-of select="substring($v, 0, string-length($v))" /></xsl:when>
			<xsl:when test="ends-with($v, 'in')"><xsl:value-of select="substring($v, 0, string-length($v) - 1)" /></xsl:when>
			<xsl:when test="ends-with($v, 'cm')"><xsl:value-of select="substring($v, 0, string-length($v) - 1)" /></xsl:when>
			<xsl:when test="ends-with($v, 'mm')"><xsl:value-of select="substring($v, 0, string-length($v) - 1)" /></xsl:when>
			<xsl:when test="ends-with($v, 'pt')"><xsl:value-of select="substring($v, 0, string-length($v) - 1)" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="$v" /></xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>