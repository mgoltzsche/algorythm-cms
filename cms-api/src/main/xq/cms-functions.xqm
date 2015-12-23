module namespace cms = "http://cms.algorythm.de/functions";
declare default element namespace "http://cms.algorythm.de/common/CMS";
declare namespace s = "http://cms.algorythm.de/common/Site";

import module namespace functx = "http://www.functx.com";

declare variable $cms:DATABASE as xs:string := 'testdb';

declare function cms:dynamic-path($parent as node(), $path as xs:string) as item()* {
	let $nextStep := functx:substring-before-if-contains($path,'/')
	let $restOfSteps := substring-after($path,'/')
	return if (contains($nextStep,'['))
		then (
			let $nodeName := substring-before($nextStep,'[')
			let $pos := number(substring-before(substring-after($nextStep,'['),']'))
			let $child := $parent/*[functx:name-test(name(),$nodeName)][$pos]
			return if ($child and $restOfSteps)
				then cms:dynamic-path($child, $restOfSteps)
				else $child
		)
		else for $child in ($parent/*[functx:name-test(name(),$nextStep)],
				$parent/@*[functx:name-test(name(), substring-after($nextStep,'@'))])
			return if ($restOfSteps)
				then cms:dynamic-path($child, $restOfSteps)
				else $child
};

declare function cms:site() as node() {
	doc($cms:DATABASE||'/cms-site.xml')/s:site
};

declare function cms:content-path($src as xs:string) as xs:string {
	$cms:DATABASE||$src
};

declare function cms:resolve-page-path($parent as node(),
		$pathSegments as xs:string*) as item()* {
	if (count($pathSegments) > 0)
		then (
			let $nextSegment := $pathSegments[1]
			let $restSegments := subsequence($pathSegments, 2)
			let $page := $parent/*[name() = $nextSegment]
			return if ($page)
				then cms:resolve-page-path($page, $restSegments)
				else ()
		)
		else $parent
};

(: 
declare function cms:resolve-page-path($parent as node(), $path as xs:string)
		as item()* {
	let $nextSegment := if (contains($path,'/'))
		then substring-before($path,'/')
		else $path
	let $restSegments := substring-after($path,'/')
	let $child := $parent/*[name() = $nextSegment]
	return if ($child and $restSegments)
		then cms:resolve-page-path($child, $restSegments)
		else $child
};
:)

declare function cms:generate-navigation($page as node(), $path as xs:string) as node()* {
	if ($page/*)
		then (
			<nav path="{$path}" title="{cms:page-title($page)}" src="{$page/@src}">
				{for $child in $page/*
					return if (boolean($child/@nav-exclude) != true())
						then <nav path="{$path||'/'||$child/name()}" title="{cms:page-title($child)}" src="{$child/@src}" />
						else ()}
			</nav>,
			for $child in $page/*
				return cms:generate-navigation($child, $path||'/'||$child/name())
		)
		else ()
};

declare function cms:generate-breadcrumbs($page as node()) as node()* {
	for $parentPage in $page/ancestor-or-self::*
		return <nav path="{cms:generate-page-path($parentPage)}" title="{cms:page-title($parentPage)}" src="{$parentPage/@src}" />
};

declare function cms:generate-page-path($page as node()) as xs:string {
	if ($page/../..)
		then cms:generate-page-path($page/..)||'/'||$page/name()
		else ''
};

declare %private function cms:page-title($page as node()) as xs:string {
	let $title := try {
		cms:derive-doc-title(doc($cms:DATABASE||$page/@src))
	} catch bxerr:BXDB0006 {
		()
	}
	return if ($title)
		then $title
		else $page/name()
};

declare function cms:derive-doc-title($doc as node()) as xs:string {
	$doc/*/@title
};