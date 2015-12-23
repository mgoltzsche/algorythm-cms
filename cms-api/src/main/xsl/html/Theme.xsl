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
		<xsl:variable name="rootPath" select="c:relative-root-path(/c:page/@path)" />
		<html ng-app="algorythm.cms">
			<head>
				<title ng-bind="pageTitle"><xsl:value-of select="@title" /></title>
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<link rel="stylesheet" href="{$rootPath}../resources/css/default-theme/main.css" />
				<script type="text/javascript">var cms = {'baseUrl': '<xsl:value-of select="$rootPath"/>'};</script>
				<script type="text/javascript" src="{$rootPath}../resources/js/cms-api.min.js"></script>
			</head>
			<body class="ng-cloak">
				<header id="header" class="collapsed">
					<nav id="nav" class="menu horizontal" cms-menu="selected" role="menubar">
						<span cms-collapse="header" class="btn btn-nav"></span>
						<a href="{$rootPath}/index.html" class="logo">
							<svg xmlns:xlink="http://www.w3.org/1999/xlink">
								<use xlink:href="{$rootPath}../resources/sprites.svg#logo" />
							</svg>
						</a>
						<xsl:call-template name="c:html-navigation" />
					</nav>
				</header>
				<main ng-view="" class="ng-cloak animate-slidehorizontal">
					<xsl:apply-templates select="c:content/*" />
				</main>
				<footer>
					Powered by <b>algorythm-cms</b>
				</footer>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
