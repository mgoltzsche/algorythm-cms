<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	exclude-result-prefixes="c">

	<xsl:template match="c:grid">
		<xsl:variable name="spacingClass" select="if (not(@spacing) or @spacing = true()) then 'spacing' else ''" />
		<xsl:variable name="columns" select="if (@columns) then number(@columns) else 1" />
		<xsl:variable name="columnsSm" select="if (@columns-sm) then number(@columns-sm) else $columns" />
		<xsl:variable name="columnsMd" select="if (@columns-md) then number(@columns-md) else $columnsSm" />
		<xsl:variable name="columnsLg" select="if (@columns-lg) then number(@columns-lg) else $columnsMd" />
		<xsl:variable name="columnsXl" select="if (@columns-xl) then number(@columns-xl) else $columnsLg" />
		<div class="pure-g {$spacingClass}">
			<xsl:apply-templates mode="grid">
				<xsl:with-param name="columns" select="$columns" />
				<xsl:with-param name="columnsSm" select="$columnsSm" />
				<xsl:with-param name="columnsMd" select="$columnsMd" />
				<xsl:with-param name="columnsLg" select="$columnsLg" />
				<xsl:with-param name="columnsXl" select="$columnsXl" />
			</xsl:apply-templates>
		</div>
	</xsl:template>
	
	<xsl:template mode="grid" match="c:cell">
		<xsl:param name="columns" />
		<xsl:param name="columnsSm" />
		<xsl:param name="columnsMd" />
		<xsl:param name="columnsLg" />
		<xsl:param name="columnsXl" />
		<xsl:variable name="span" select="if (@span) then number(@span) else 1" />
		<xsl:variable name="spanSm" select="if (@span-sm) then number(@span-sm) else $span" />
		<xsl:variable name="spanMd" select="if (@span-md) then number(@span-md) else $spanSm" />
		<xsl:variable name="spanLg" select="if (@span-lg) then number(@span-lg) else $spanMd" />
		<xsl:variable name="spanXl" select="if (@span-xl) then number(@span-xl) else $spanLg" />
		<xsl:variable name="suffixSm">
			<xsl:call-template name="c:reduce-grid-fraction">
				<xsl:with-param name="numerator" select="$spanSm" />
				<xsl:with-param name="denominator" select="$columnsSm" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="suffix">
			<xsl:call-template name="c:reduce-grid-fraction">
				<xsl:with-param name="numerator" select="$span" />
				<xsl:with-param name="denominator" select="$columns" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="suffixMd">
			<xsl:call-template name="c:reduce-grid-fraction">
				<xsl:with-param name="numerator" select="$spanMd" />
				<xsl:with-param name="denominator" select="$columnsMd" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="suffixLg">
			<xsl:call-template name="c:reduce-grid-fraction">
				<xsl:with-param name="numerator" select="$spanLg" />
				<xsl:with-param name="denominator" select="$columnsLg" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="suffixXl">
			<xsl:call-template name="c:reduce-grid-fraction">
				<xsl:with-param name="numerator" select="$spanXl" />
				<xsl:with-param name="denominator" select="$columnsXl" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="styleClasses">
			<xsl:text disable-output-escaping="yes">pure-u-</xsl:text>
			<xsl:value-of select="$suffix" />
			<xsl:text disable-output-escaping="yes"> </xsl:text>
			<xsl:if test="$suffixSm != $suffix">
				<xsl:text disable-output-escaping="yes">pure-u-sm-</xsl:text>
				<xsl:value-of select="$suffixSm" />
				<xsl:text disable-output-escaping="yes"> </xsl:text>
			</xsl:if>
			<xsl:if test="$suffixMd != $suffixSm">
				<xsl:text disable-output-escaping="yes">pure-u-md-</xsl:text>
				<xsl:value-of select="$suffixMd" />
				<xsl:text disable-output-escaping="yes"> </xsl:text>
			</xsl:if>
			<xsl:if test="$suffixLg != $suffixMd">
				<xsl:text disable-output-escaping="yes">pure-u-lg-</xsl:text>
				<xsl:value-of select="$suffixLg" />
				<xsl:text disable-output-escaping="yes"> </xsl:text>
			</xsl:if>
			<xsl:if test="$suffixXl != $suffixLg">
				<xsl:text disable-output-escaping="yes">pure-u-xl-</xsl:text>
				<xsl:value-of select="$suffixXl" />
				<xsl:text disable-output-escaping="yes"> </xsl:text>
			</xsl:if>
		</xsl:variable>
		<div class="{$styleClasses}" contenteditable="false">
			<xsl:choose>
				<xsl:when test="not(../@spacing) or @spacing = true()">
					<div>
						<xsl:apply-templates />
					</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
	
	<xsl:template name="c:reduce-grid-fraction">
		<xsl:param name="numerator" />
		<xsl:param name="denominator" />
		<xsl:call-template name="c:reduce-grid-fraction-by-value">
			<xsl:with-param name="numerator" select="$numerator" />
			<xsl:with-param name="denominator" select="$denominator" />
			<xsl:with-param name="value" select="if ($numerator lt $denominator) then $numerator else $denominator" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="c:reduce-grid-fraction-by-value">
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
				<xsl:call-template name="c:reduce-grid-fraction">
					<xsl:with-param name="numerator" select="$numerator div $value" />
					<xsl:with-param name="denominator" select="$denominator div $value" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="c:reduce-grid-fraction-by-value">
					<xsl:with-param name="numerator" select="$numerator" />
					<xsl:with-param name="denominator" select="$denominator" />
					<xsl:with-param name="value" select="$value - 1" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>