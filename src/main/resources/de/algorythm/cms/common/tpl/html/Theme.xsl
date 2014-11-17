<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns:p="http://cms.algorythm.de/common/Pages"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c p">
	<xsl:strip-space elements="*"/>
	<xsl:param name="outputDirectory" />
	<xsl:param name="pagesFile" />
	<xsl:param name="relativeBaseUrl" />
	<xsl:param name="resourceDirectory" />

	<xsl:variable name="content">
		<div>
			<xsl:attribute name="cms-page-title">
				<xsl:value-of select="(/c:page | /c:site)/@title" />
			</xsl:attribute>
			<xsl:apply-templates select="/c:page/* | /c:site/*" />
		</div>
	</xsl:variable>

	<xsl:template match="/c:page | /c:site">
		<html ng-app="cms">
			<head>
				<title ng-bind="'algorythm.de :: ' + pageTitle">
					<xsl:value-of select="current()/@title" />
				</title>
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<link rel="stylesheet" href="{$resourceDirectory}/css/default-theme/main.css" />
				<!-- <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.0/angular.js"></script>
				<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.0/angular-route.js"></script>-->
				<script type="text/javascript">var cms = {'baseUrl': '<xsl:value-of select="$relativeBaseUrl"></xsl:value-of>'};</script>
				<script src="{$resourceDirectory}/scripts/angular.js"></script>
				<script src="{$resourceDirectory}/scripts/angular-route.js"></script>
				<script src="{$resourceDirectory}/scripts/cms.js"></script>
			</head>
			<body>
				<div id="container">
					<nav class="pure-menu pure-menu-open pure-menu-horizontal">
						<a href="#/" class="pure-menu-heading">Site Title</a>
						<ul cms-menu="pure-menu-selected">
							<xsl:apply-templates select="document('/pages.xml')/p:page/*">
								<xsl:with-param name="maxDepth" select="1" />
							</xsl:apply-templates>
						</ul>
					</nav>
					<xsl:if test="current()/@subNav='true'">
						<nav id="secondary-nav"></nav>
					</xsl:if>
					<div>
						<h2 ng-bind="pageTitle">
							<xsl:value-of select="current()/@title" />
						</h2>
						<main ng-view="">
							<xsl:copy-of select="$content" />
						</main>
						<xsl:result-document href="{$outputDirectory}/content.html">
							<div>
								<xsl:copy-of select="$content" />
							</div>
						</xsl:result-document>
					</div>
				</div>
			</body>
			</html>
	</xsl:template>
</xsl:stylesheet>
