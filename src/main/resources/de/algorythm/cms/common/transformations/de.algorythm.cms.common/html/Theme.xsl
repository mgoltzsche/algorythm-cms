<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Page"
	xmlns:l="http://cms.algorythm.de/common/Locales"
	exclude-result-prefixes="c p l">
	<xsl:output method="html"/>
	<xsl:param name="relativeBaseURL" />
	<xsl:param name="resourceBaseURL" />
	<xsl:param name="site.internationalized" />
	<xsl:param name="site.name" />
	<xsl:param name="page.path" />
	<xsl:param name="page.locale" />
	<xsl:param name="site.param.testparam" />
	
	<xsl:template match="p:page">
		<xsl:variable name="page.title" select="document(concat('metadata:', @content))/*/@title" />
		<xsl:variable name="content">
			<xsl:variable name="includedContent">
				<xsl:call-template name="c:include-localized">
					<xsl:with-param name="uri" select="@content" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:apply-templates select="$includedContent">
				<!--<xsl:with-param name="showTitle" select="true()" />-->
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:result-document href="content.html">
			<div cms-page-title="{$page.title}">
				<xsl:copy-of select="$content" />
			</div>
		</xsl:result-document>
		
		<html ng-app="algorythm.cms">
			<head>
				<title ng-bind="pageTitle + ' - {$site.name}'">
					<xsl:value-of select="$page.title" />
				</title>
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<link rel="stylesheet" href="{$resourceBaseURL}/main.css" />
				<script type="text/javascript">var cms = {'baseUrl': '<xsl:value-of select="$relativeBaseURL"></xsl:value-of>'};</script>
				<script src="{$resourceBaseURL}/main.js"></script>
			</head>
			<body class="ng-cloak">
				<header id="header" class="collapsed">
					<xsl:if test="$site.internationalized">
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
					</xsl:if>
					<nav id="nav" class="menu horizontal" cms-menu="selected" role="menubar">
						<span cms-collapse="header" class="btn btn-nav"></span>
						<a href="{$relativeBaseURL}/index.html" title="{$site.name}" class="logo">
							<svg xmlns:xlink="http://www.w3.org/1999/xlink">
								<use xlink:href="{$resourceBaseURL}/sprites.svg#logo" />
							</svg>
						</a>
						<xsl:call-template name="c:menu-html">
							<xsl:with-param name="maxDepth" select="0" />
							<xsl:with-param name="id" select="'main-nav'" />
						</xsl:call-template>
					</nav>
				</header>
				<main ng-view="" class="ng-cloak animate-flythrough">
					<xsl:copy-of select="$content" />
				</main>
				<footer>
					Generated with algorythm-cms
				</footer>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
