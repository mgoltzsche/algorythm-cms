<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	xmlns:l="http://cms.algorythm.de/common/Locales"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c p">
	<xsl:strip-space elements="*"/>
	<xsl:param name="relativeBaseURL" />
	<xsl:param name="resourceBaseURL" />
	<xsl:param name="site.name" />
	<xsl:param name="page.path" />
	<xsl:param name="page.title" />
	<xsl:param name="site.param.testparam" />

	<xsl:template match="/c:page">
		<html ng-app="cms">
			<head>
				<title ng-bind="pageTitle + ' - {$site.name}'">
					<xsl:value-of select="current()/@title" />
				</title>
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<link rel="stylesheet" href="{$resourceBaseURL}/main.css" />
				<script type="text/javascript">var cms = {'baseUrl': '<xsl:value-of select="$relativeBaseURL"></xsl:value-of>'};</script>
				<script src="{$resourceBaseURL}/main.js"></script>
			</head>
			<body>
				<div id="container">
					<ul>
						<xsl:for-each select="document('/supported-locales.xml')/l:locales/l:locale">
							<li><a href="{$relativeBaseURL}/../{@id}{$page.path}/index.html" cms-language="{@id}"><xsl:value-of select="@id" /> - <xsl:value-of select="@title" /></a></li>
						</xsl:for-each>
					</ul>
					<nav class="pure-menu pure-menu-open pure-menu-horizontal" cms-menu="pure-menu-selected">
						<a href="{$relativeBaseURL}/index.html" class="pure-menu-heading"><xsl:value-of select="$site.name" /></a>
						<xsl:call-template name="c:menu">
							<xsl:with-param name="maxDepth" select="1" />
						</xsl:call-template>
					</nav>
					<xsl:if test="current()/@subNav='true'">
						<nav id="secondary-nav"></nav>
					</xsl:if>
					<div>
						<h2 ng-bind="pageTitle">
							<xsl:value-of select="$page.title" />
						</h2>
						<xsl:variable name="content">
							<i><xsl:value-of select="$site.param.testparam" /></i>
							<xsl:apply-templates select="/c:page/*" />
						</xsl:variable>
						<main ng-view="">
							<xsl:copy-of select="$content" />
						</main>
						<xsl:result-document href="content.html">
							<div cms-page-path="{$page.path}" cms-page-title="{$page.title}">
								<xsl:copy-of select="$content" />
							</div>
						</xsl:result-document>
					</div>
				</div>
			</body>
			</html>
	</xsl:template>
</xsl:stylesheet>