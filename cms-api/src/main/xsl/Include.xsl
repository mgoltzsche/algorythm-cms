<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:b="http://cms.algorythm.de/common/Bundle"
	xmlns:o="urn:oasis:names:tc:opendocument:xmlns:office:1.0">
	<xsl:param name="page.locale" />
	
	<xsl:template name="c:include-localized">
		<xsl:param name="uri" />
		<xsl:param name="baseUri" select="'file:/'"/>
		<xsl:if test="not($page.locale)">
			<!-- Assert parameter existence -->
			<xsl:message terminate="yes">Error: Undefined parameter page.locale!</xsl:message>
		</xsl:if>
		<xsl:if test="not($uri)">
			<xsl:message terminate="yes">Error: Undefined include URI!</xsl:message>
		</xsl:if>
		<xsl:variable name="absoluteUri" select="resolve-uri($uri, $baseUri)" />
		<xsl:variable name="absolutePath" select="substring-after($absoluteUri, ':')" />
		<!--<xsl:variable name="i18nUri" select="if (starts-with($absolutePath, '/i18n/')) then $absoluteUri else concat('file:/i18n/', $page.locale, $absolutePath)" />-->
		<xsl:apply-templates mode="c:include" select="document($absoluteUri)">
			<xsl:with-param name="baseUri" select="$absoluteUri" />
			<xsl:with-param name="customContent" select="." />
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template mode="c:include" match="c:include">
		<xsl:param name="baseUri" />
		<xsl:call-template name="c:include-localized">
			<xsl:with-param name="uri" select="@href" />
			<xsl:with-param name="baseUri" select="$baseUri" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template mode="c:include" match="c:customizable">
		<xsl:param name="baseUri" />
		<xsl:param name="customContent" />
		<xsl:variable name="replacement" select="$customContent/*[current()/@placeholder=@placeholder]" />
		<xsl:choose>
			<xsl:when test="$replacement">
				<xsl:apply-templates mode="c:include" select="$replacement/* | $replacement/text()">
					<xsl:with-param name="baseUri" select="$baseUri" />
					<xsl:with-param name="customContent" select="$customContent" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="c:include">
					<xsl:with-param name="baseUri" select="$baseUri" />
					<xsl:with-param name="customContent" select="$customContent" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template mode="c:include" match="*">
		<xsl:param name="baseUri" />
		<xsl:param name="customContent" />
		<xsl:copy>
			<xsl:for-each select="@*">
                <xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
            </xsl:for-each>
            <xsl:apply-templates mode="c:include">
            	<xsl:with-param name="baseUri" select="$baseUri" />
				<xsl:with-param name="customContent" select="$customContent" />
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template> 
</xsl:stylesheet>
