<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:m="http://cms.algorythm.de/common/Math"
	exclude-result-prefixes="m">

	<xsl:template name="m:reduce-fraction">
		<xsl:param name="numerator" />
		<xsl:param name="denominator" />
		<xsl:call-template name="m:reduce-fraction-by-value">
			<xsl:with-param name="numerator" select="$numerator" />
			<xsl:with-param name="denominator" select="$denominator" />
			<xsl:with-param name="value" select="if ($numerator lt $denominator) then $numerator else $denominator" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="m:reduce-fraction-by-value">
		<xsl:param name="numerator" />
		<xsl:param name="denominator" />
		<xsl:param name="value" />
		<xsl:choose>
			<xsl:when test="$value le 1">
				<xsl:choose>
					<xsl:when test="$numerator ge $denominator">
						<xsl:value-of select="'1-1'" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($numerator, '-', $denominator)" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$numerator mod $value = 0 and $denominator mod $value = 0">
				<xsl:call-template name="m:reduce-fraction">
					<xsl:with-param name="numerator" select="$numerator div $value" />
					<xsl:with-param name="denominator" select="$denominator div $value" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="m:reduce-fraction-by-value">
					<xsl:with-param name="numerator" select="$numerator" />
					<xsl:with-param name="denominator" select="$denominator" />
					<xsl:with-param name="value" select="$value - 1" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>