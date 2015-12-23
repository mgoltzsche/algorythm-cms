<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c">
	<xsl:output method="html"/>
	<!-- <xsl:key name="pageIndex" match="/c:page" use="concat(@path, '/')"/>-->
	
	<xsl:template name="c:html-navigation">
		<xsl:param name="path" select="''" />
		<xsl:param name="depth" select="1" />
		<xsl:param name="maxDepth" select="0" />
		<xsl:param name="id" />
		<xsl:variable name="nav" select="/c:page/c:navigation/c:nav[@path = $path]" />
		<xsl:variable name="baseUrl" select="c:relative-root-path(/c:page/@path)" />
		
		<ul role="menu">
			<xsl:if test="$id">
				<xsl:attribute name="id" select="$id" />
			</xsl:if>
			<xsl:for-each select="$nav/*">
				<xsl:if test="boolean(@nav-exclude) != true()">
					<xsl:variable name="navEntryPath" select="@path" />
					<xsl:variable name="hasChildren" select="($maxDepth lt 1 or $depth lt $maxDepth) and /c:page/c:navigation/c:nav[@path = $navEntryPath and boolean(@nav-exclude) != true()]" />
					<xsl:variable name="expandId" select="generate-id()" />
					<xsl:variable name="renderPath" select="/c:page/@path" />
					<li class="{if (@path = $renderPath or starts-with($renderPath, concat($navEntryPath, '/'))) then 'selected' else ''} {if ($hasChildren) then 'folder' else ''}" role="menuitem">
						<xsl:variable name="relativePath" select="if (starts-with(@path, '/')) then substring(@path, 2) else @path" />
						<xsl:if test="$hasChildren">
							<span cms-collapse="{$expandId}" class="btn btn-expand"></span>
						</xsl:if>
						<a href="{$baseUrl}{$relativePath}/index.html" edit-page-title="{@src}:*/@title">
							<xsl:value-of select="@title" />
						</a>
						<xsl:if test="$hasChildren">
							<xsl:call-template name="c:html-navigation">
								<xsl:with-param name="path" select="@path" />
								<xsl:with-param name="depth" select="$depth + 1" />
								<xsl:with-param name="maxDepth" select="$maxDepth" />
								<xsl:with-param name="id" select="$expandId" />
							</xsl:call-template>
						</xsl:if>
					</li>
				</xsl:if>
			</xsl:for-each>
		</ul>
	</xsl:template>

	<xsl:template name="c:html-breadcrumb-navigation">
		<xsl:variable name="currentPath" select="/c:page/@path" />
		<xsl:variable name="baseUrl" select="c:relative-root-path($currentPath)" />
		<ul class="breadcrumbs">
			<xsl:for-each select="/c:page/c:breadcrumbs/*">
				<xsl:variable name="relativePath" select="if (starts-with(@path, '/')) then substring(@path, 2) else @path" />
				<li class="{if (position() = 1) then if (position() = last()) then 'breadcrumb first last' else 'breadcrumb first' else if (position() = last()) then 'breadcrumb last' else 'breadcrumb'}">
					<a href="{$baseUrl}{$relativePath}/index.html" class="{if (@path = $currentPath) then 'last' else ''}">
						<xsl:value-of select="@title" />
					</a>
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>

	<xsl:function name="c:absolute-path">
		<xsl:param name="path" />
		<xsl:param name="basePath" />
		<xsl:variable name="baseUri" select="if (ends-with($basePath, '/')) then $basePath else concat($basePath, '/')" />
		<xsl:variable name="pageUri" select="resolve-uri($path, $baseUri)" />
		<xsl:value-of select="substring($pageUri, 6, string-length($pageUri) - 6)" />
	</xsl:function>

	<xsl:function name="c:relative-root-path">
		<xsl:param name="path" />
		<xsl:text>./</xsl:text>
		<xsl:for-each select="1 to string-length($path) - string-length(translate($path,'/','')) - 1">
			<xsl:text>../</xsl:text>
		</xsl:for-each>
	</xsl:function>

	<!-- nav element -->
	<xsl:template match="c:nav">
		<nav class="menu vertical" role="menubar">
			<xsl:if test="@title">
				<h2><xsl:value-of select="@title" /></h2>
			</xsl:if>
			<xsl:call-template name="c:html-navigation">
				<xsl:with-param name="path" select="c:absolute-path(if (@parent) then @parent else '.', concat('page:', /c:page/@path))" />
				<xsl:with-param name="maxDepth" select="if (@depth) then number(@depth) else 1" />
			</xsl:call-template>
		</nav>
	</xsl:template>
</xsl:stylesheet>
