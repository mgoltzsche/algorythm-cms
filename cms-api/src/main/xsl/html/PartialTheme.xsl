<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:h="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c h">
	<xsl:import href="Components.xsl" />
	<xsl:output method="xml" />

	<xsl:template match="c:page">
		<div cms-page-title="{@title}">
			<xsl:apply-templates select="c:content/*" />
		</div>
	</xsl:template>
</xsl:stylesheet>
