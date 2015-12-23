module namespace render = "http://cms.algorythm.de/web/render";
declare default element namespace "http://cms.algorythm.de/common/CMS";

import module namespace cms = "http://cms.algorythm.de/functions";

declare
%rest:path("page/{$path=.*}")
%rest:GET
%output:method("xhtml")
%output:omit-xml-declaration("no")
%output:doctype-public("-//W3C//DTD XHTML 1.0 Transitional//EN")
%output:doctype-system("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd")
function render:request($path as xs:string) {
	let $urlSegments := fn:tokenize($path, '/')
	let $lastUrlSegment := $urlSegments[last()] 
	let $pathSegments := fn:subsequence($urlSegments, 1, fn:count($urlSegments) - 1)
	let $site := cms:site()
	let $page := cms:resolve-page-path($site, $pathSegments)
	let $contentUri := $page/@src
	let $renderPartial := $lastUrlSegment = 'content.html'
	let $theme := if ($renderPartial)
		then 'http://cms.algorythm.de/cms-api/html/PartialTheme.xsl'
		else 'http://cms.algorythm.de/cms-api/html/Theme.xsl'
	return if ($contentUri and ($renderPartial or $lastUrlSegment = 'index.html'))
		then (
			let $doc := doc(cms:content-path($contentUri))
			return map:get(fn:transform(map{
				'stylesheet-location': $theme,
				'source-node': <page name="{$page/name()}" path="/{$path}" title="{cms:derive-doc-title($doc)}">
					<breadcrumbs>
						{cms:generate-breadcrumbs($page)}
					</breadcrumbs>
					<navigation>
						{cms:generate-navigation($site,'')}
					</navigation>
					<content src="{$contentUri}">
						{$doc}
					</content>
				</page>
			}), QName('', 'output'))
		) else (
			'Nooooo! '||$path||' not found'
		)
};