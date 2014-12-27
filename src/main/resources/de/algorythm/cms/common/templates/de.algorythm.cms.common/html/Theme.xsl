<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	xmlns:b="http://cms.algorythm.de/common/Bundle"
	xmlns:l="http://cms.algorythm.de/common/Locales"
	exclude-result-prefixes="c p l">
	<xsl:param name="relativeBaseURL" />
	<xsl:param name="resourceBaseURL" />
	<xsl:param name="site.name" />
	<xsl:param name="page.path" />
	<xsl:param name="page.title" />
	<xsl:param name="page.locale" />
	<xsl:param name="site.param.testparam" />
	
	<xsl:template match="b:page">
		<xsl:variable name="content">
			<xsl:variable name="includedContent">
				<xsl:call-template name="c:include-localized">
					<xsl:with-param name="uri" select="@content" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:apply-templates select="$includedContent">
				<xsl:with-param name="showTitle" select="false()" />
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:result-document href="content.html">
			<div cms-page-title="{$page.title}">
				<xsl:copy-of select="$content" />
			</div>
		</xsl:result-document>
		
		<html ng-app="cms">
			<head>
				<title ng-bind="pageTitle + ' - {$site.name}'">
					<xsl:value-of select="$page.title" />
				</title>
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<link rel="stylesheet" href="{$resourceBaseURL}/main.css" />
				<script type="text/javascript">var cms = {'baseUrl': '<xsl:value-of select="$relativeBaseURL"></xsl:value-of>'};</script>
				<script src="{$resourceBaseURL}/main.js"></script>
			</head>
			<body>
				<div id="container">
					<ul class="locale-switch">
						<xsl:for-each select="document(concat('/', $page.locale, '/supported-locales.xml'))/l:locales/l:locale">
							<li>
								<xsl:if test="@active = true()">
									<xsl:attribute name="class" select="'active'" />
								</xsl:if>
								<a href="{$relativeBaseURL}/../{@id}{$page.path}/index.html" cms-language="{@language}" lang="{@language}" title="{@title}">
									<svg xmlns:xlink="http://www.w3.org/1999/xlink">
										<use xlink:href="{$resourceBaseURL}/sprites.svg#{@country}" />
									</svg>
								</a>
							</li>
						</xsl:for-each>
					</ul>
					<img src="{$resourceBaseURL}/sprites.svg#logo" />
					<img src="{$resourceBaseURL}/sprites.svg#example" />
					<svg class="icon" xmlns:xlink="http://www.w3.org/1999/xlink">
						<use xlink:href="{$resourceBaseURL}/sprites.svg#example" />
					</svg>
					<svg class="icon" xmlns:xlink="http://www.w3.org/1999/xlink">
						<use xlink:href="{$resourceBaseURL}/sprites.svg#logo" />
					</svg>
					<nav class="pure-menu pure-menu-open pure-menu-horizontal" cms-menu="pure-menu-selected" role="menubar">
						<a href="{$relativeBaseURL}/index.html" class="pure-menu-heading"><xsl:value-of select="$site.name" /></a>
						<xsl:call-template name="c:menu-html">
							<xsl:with-param name="maxDepth" select="1" />
						</xsl:call-template>
					</nav>
					<h1 ng-bind="pageTitle">
						<xsl:value-of select="$page.title" />
					</h1>
					<div>
						<main ng-view="">
							<xsl:copy-of select="$content" />
						</main>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
