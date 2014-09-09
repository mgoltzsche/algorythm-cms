<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:p="http://cms.algorythm.de/common/Page"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="p">
	<xsl:include href="../include-view-html.xsl" />

	<xsl:template match="/">
		<div>
			<xsl:if test="/p:page/@subNav='true'">
				<nav id="secondary-nav"></nav>
			</xsl:if>
			<main>
				<h3>
					<xsl:value-of select="/p:page/@title" />
				</h3>
				<xsl:apply-imports />
			</main>
		</div>
	</xsl:template>
</xsl:stylesheet>
