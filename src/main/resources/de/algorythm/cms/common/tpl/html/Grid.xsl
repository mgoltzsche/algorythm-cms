<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:m="http://cms.algorythm.de/common/Math"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c m">

	<xsl:template match="c:grid">
		<div class="pure-g">
			<xsl:variable name="columns" select="if (./@columns) then number(./@columns) else 1" />
			<xsl:variable name="columnsSm" select="if (./@columns-sm) then number(./@columns-sm) else $columns" />
			<xsl:variable name="columnsMd" select="if (./@columns-md) then number(./@columns-md) else $columnsSm" />
			<xsl:variable name="columnsLg" select="if (./@columns-lg) then number(./@columns-lg) else $columnsMd" />
			<xsl:variable name="columnsXl" select="if (./@columns-xl) then number(./@columns-xl) else $columnsLg" />
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
		<xsl:variable name="span" select="if (./@span) then number(./@span) else 1" />
		<xsl:variable name="spanSm" select="if (./@span-sm) then number(./@span-sm) else $span" />
		<xsl:variable name="spanMd" select="if (./@span-md) then number(./@span-md) else $spanSm" />
		<xsl:variable name="spanLg" select="if (./@span-lg) then number(./@span-lg) else $spanMd" />
		<xsl:variable name="spanXl" select="if (./@span-xl) then number(./@span-xl) else $spanLg" />
		<xsl:variable name="styleClasses">
			pure-u-<xsl:call-template name="m:reduce-fraction">
				<xsl:with-param name="numerator" select="$span" />
				<xsl:with-param name="denominator" select="$columns" />
			</xsl:call-template>
			<xsl:if test="$spanSm != $span or $columnsSm != $columns">
				pure-u-sm-<xsl:call-template name="m:reduce-fraction">
					<xsl:with-param name="numerator" select="$spanSm" />
					<xsl:with-param name="denominator" select="$columnsSm" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$spanMd != $spanSm or $columnsMd != $columnsSm">
				pure-u-md-<xsl:call-template name="m:reduce-fraction">
					<xsl:with-param name="numerator" select="$spanMd" />
					<xsl:with-param name="denominator" select="$columnsMd" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$spanLg != $spanMd or $columnsLg != $columnsMd">
				pure-u-lg-<xsl:call-template name="m:reduce-fraction">
					<xsl:with-param name="numerator" select="$spanLg" />
					<xsl:with-param name="denominator" select="$columnsLg" />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$spanXl != $spanLg or $columnsXl != $columnsLg">
				pure-u-xl-<xsl:call-template name="m:reduce-fraction">
					<xsl:with-param name="numerator" select="$spanXl" />
					<xsl:with-param name="denominator" select="$columnsXl" />
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>
		<div class="{normalize-space($styleClasses)}">
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>