<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:m="http://algorythm.de/cms/example/Hello">
	<xsl:function name="m:hello">
		<xsl:value-of select="m:hello('Universe')"/>
	</xsl:function>
</xsl:stylesheet>
