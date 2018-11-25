<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:exmpl="http://example.org/hello">

	<xsl:function name="exmpl:path">
		<xsl:param name="name" />
		<xsl:value-of select="concat('Hello ', $name)" />
	</xsl:function>
</xsl:stylesheet>
