<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	exclude-result-prefixes="c p">
	<xsl:param name="relativeBaseUrl" />
	<xsl:param name="page.path" />
	<xsl:param name="page.locale" />

	<xsl:variable name="c:pages" select="document(concat('/', $page.locale, '/pages.xml'))" />
	<!-- <xsl:key name="pageIndex" match="p:page" use="concat(@path, '/')"/>-->
	
	<xsl:template name="c:menu-html">
		<xsl:param name="root" select="$c:pages/*" />
		<xsl:param name="depth" select="1" />
		<xsl:param name="maxDepth" select="0" />
		
		<ul role="menu">
			<xsl:for-each select="$root/*">
				<xsl:if test="boolean(@nav-contained) = true()">
					<xsl:variable name="active" select="@path = $page.path or starts-with($page.path, concat(@path, '/'))" />
					<li class="{if ($active) then 'selected' else ''}" role="menuitem">
						<xsl:call-template name="c:link" />
						<xsl:if test="* and ($maxDepth lt 1 or $depth lt $maxDepth)">
							<xsl:call-template name="c:menu-html">
								<xsl:with-param name="root" select="." />
								<xsl:with-param name="depth" select="$depth + 1" />
								<xsl:with-param name="maxDepth" select="$maxDepth" />
							</xsl:call-template>
						</xsl:if>
					</li>
				</xsl:if>
			</xsl:for-each>
		</ul>
	</xsl:template>
	
	<xsl:function name="c:absolute-path">
		<xsl:param name="path" />
		<xsl:variable name="baseUri" select="concat('page:', $page.path, '/')" />
		<xsl:variable name="pagePath" select="if (ends-with($path, '/')) then $path else concat($path, '/')" />
		<xsl:variable name="pageUri" select="resolve-uri($pagePath, $baseUri)" />
		<xsl:value-of select="substring($pageUri, 6, string-length($pageUri) - 6)" />
	</xsl:function>
	
	<xsl:template name="c:link">
		<xsl:param name="page" select="." />
		<a href="{$relativeBaseURL}{$page/@path}/index.html">
			<xsl:value-of select="$page/@short-title" />
		</a>
	</xsl:template>
	
	<!-- <xsl:template name="c:page-by-path">
		<xsl:param name="path" select="'.'" />
		<xsl:variable name="absolutePath">
			<xsl:call-template name="c:absolute-path">
				<xsl:with-param name="path" select="$path" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:for-each select="$pages">
			<xsl:copy-of select="id($absolutePath)" />
		</xsl:for-each>
	</xsl:template>-->
</xsl:stylesheet>
