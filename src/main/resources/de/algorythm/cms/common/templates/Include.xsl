<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS">
	<xsl:template match="c:include">
		<xsl:variable name="includeOutput">
			<xsl:apply-templates mode="include" select=".">
				<xsl:with-param name="baseUri" select="'file:/'"/>
			</xsl:apply-templates>
		</xsl:variable>
		<!--<xsl:copy-of select="$includeOutput" />-->
		<xsl:apply-templates select="$includeOutput" />
	</xsl:template>
	
	<xsl:template mode="include" match="c:include">
		<xsl:param name="baseUri" />
		<xsl:variable name="absoluteHrefUri" select="resolve-uri(@href, $baseUri)" />
		<xsl:apply-templates mode="include" select="document($absoluteHrefUri)">
			<xsl:with-param name="baseUri" select="$absoluteHrefUri" />
			<xsl:with-param name="customContent" select="." />
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template mode="include" match="c:customizable">
		<xsl:param name="baseUri" />
		<xsl:param name="customContent" />
		<xsl:variable name="replacement" select="$customContent/*[current()/@placeholder=@placeholder]" />
		<xsl:choose>
			<xsl:when test="$replacement">
				<xsl:apply-templates mode="include" select="$replacement/* | $replacement/text()">
					<xsl:with-param name="baseUri" select="$baseUri" />
					<xsl:with-param name="customContent" select="$customContent" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="include">
					<xsl:with-param name="baseUri" select="$baseUri" />
					<xsl:with-param name="customContent" select="$customContent" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template mode="include" match="*">
		<xsl:param name="baseUri" />
		<xsl:param name="customContent" />
		<xsl:copy>
			<xsl:for-each select="@*">
                <xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
            </xsl:for-each>
            <xsl:apply-templates mode="include">
            	<xsl:with-param name="baseUri" select="$baseUri" />
				<xsl:with-param name="customContent" select="$customContent" />
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template> 
</xsl:stylesheet>
