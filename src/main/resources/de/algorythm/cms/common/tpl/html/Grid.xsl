<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:m="http://cms.algorythm.de/common/Math"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c m">

	<xsl:template match="c:grid">
		<div class="pure-g">
			<xsl:apply-templates mode="grid">
				<xsl:with-param name="columns" select="number(./@columns)" />
			</xsl:apply-templates>
		</div>
	</xsl:template>
	
	<xsl:template mode="grid" match="c:cell">
		<xsl:param name="columns" select="1" />
		<xsl:variable name="styleClasses">
			pure-u-<xsl:call-template name="c:grid-style-class">
				<xsl:with-param name="span" select="./@span" />
				<xsl:with-param name="columns" select="$columns" />
				<xsl:with-param name="factor" select="0.3" />
			</xsl:call-template>
			pure-u-md-<xsl:call-template name="c:grid-style-class">
				<xsl:with-param name="span" select="./@span" />
				<xsl:with-param name="columns" select="$columns" />
				<xsl:with-param name="factor" select="0.7" />
			</xsl:call-template>
			pure-u-lg-<xsl:call-template name="c:grid-style-class">
				<xsl:with-param name="span" select="./@span" />
				<xsl:with-param name="columns" select="$columns" />
				<xsl:with-param name="factor" select="1" />
			</xsl:call-template>
			pure-u-xl-<xsl:call-template name="c:grid-style-class">
				<xsl:with-param name="span" select="./@span" />
				<xsl:with-param name="columns" select="$columns" />
				<xsl:with-param name="factor" select="1.5" />
			</xsl:call-template>
		</xsl:variable>
		<div class="{normalize-space($styleClasses)}">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template name="c:grid-style-class">
		<xsl:param name="span" />
		<xsl:param name="columns" />
		<xsl:param name="factor" />
		<xsl:call-template name="m:reduce-fraction">
			<xsl:with-param name="numerator" select="number($span)" />
			<xsl:with-param name="denominator" select="round(number($factor) * $columns) - round(number($factor) * $columns) mod 2" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>