<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:c="http://cms.algorythm.de/common/CMS">

	<xsl:function name="c:path">
		<xsl:param name="node" as="node()" />
		<!--<xsl:value-of select="concat(document-uri($node),':')" /> -->
		<xsl:for-each select="$node/ancestor-or-self::*">
			<xsl:text disable-output-escaping="yes">/*</xsl:text>
			<!--<xsl:if test="count(../*) > 1"> -->
			<xsl:text disable-output-escaping="yes">[</xsl:text>
			<xsl:value-of select="count(preceding-sibling::*) + 1" />
			<xsl:text disable-output-escaping="yes">]</xsl:text>
			<!--</xsl:if> -->
		</xsl:for-each>
		<xsl:if test="count($node|$node/../@*)=count($node/../@*)">
			<xsl:value-of select="concat('/@', $node/name())" />
		</xsl:if>
	</xsl:function>

	<!-- Dynamically evaluates a simple XPath path @author Priscilla Walmsley, 
		Datypic @version 1.0 @see http://www.xsltfunctions.com/xsl/functx_dynamic-path.html 
		@param $parent the root to start from @param $path the path expression -->
	<xsl:function name="c:dynamic-path" as="item()*"
		xmlns:functx="http://www.functx.com">
		<xsl:param name="parent" as="node()" />
		<xsl:param name="path" as="xs:string" />
		<xsl:variable name="nextStep"
			select="functx:substring-before-if-contains($path,'/')" />
		<xsl:variable name="restOfSteps" select="substring-after($path,'/')" />
		<xsl:choose>
			<xsl:when test="contains($nextStep,'[')">
				<xsl:variable name="nodeName"
					select="substring-before($nextStep,'[')" />
				<xsl:variable name="pos"
					select="number(substring-before(substring-after($nextStep,'['),']'))" />
				<xsl:variable name="child"
					select="if ($nodeName='*')
						then $parent/*[$pos]
						else $parent/*[name()=$nodeName][$pos]" />
				<!--<xsl:value-of select="concat($nodeName,'[', $pos, ']   ', $restOfSteps, '    ', $child/name())" />-->
				<xsl:sequence
					select="if ($child and $restOfSteps)
						then c:dynamic-path($child, $restOfSteps)
						else $child" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each
					select="
						($parent/*[functx:name-test(name(),$nextStep)],
						$parent/@*[functx:name-test(name(),substring-after($nextStep,'@'))])">
					<xsl:variable name="child" select="." />
					<xsl:sequence
						select="if ($restOfSteps)
							then c:dynamic-path($child,$restOfSteps)
							else $child" />
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>
