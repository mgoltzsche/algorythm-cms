module namespace update = "http://cms.algorythm.de/web/update";
declare default element namespace "http://cms.algorythm.de/web/update";

import module namespace cms = "http://cms.algorythm.de/functions";

declare
%rest:GET
%rest:path('update/value/{$path=.+}')
%rest:query-param('xpath', '{$xpath}')
%rest:query-param('value', '{$value}')
%output:method('text')
(: %rest:form-param('xpath', '{$xpath}')
   %rest:form-param('value', '{$value}') :)
%updating
function update:update-value($path as xs:string, $xpath as xs:string, $value as xs:string) {
	let $contentSrc := cms:content-path('/'||$path)
	return try {
		let $doc := doc($contentSrc)
		let $alterNode := cms:dynamic-path($doc, $xpath)
		return if ($alterNode and $value)
			then (replace value of node $alterNode with $value)
			else fn:error(xs:QName('error'), 'Invalid arguments')
	} catch bxerr:BXDB0006 {
		fn:error(xs:QName('error'), 'Missing document: '||$contentSrc, 401)
	}
};

