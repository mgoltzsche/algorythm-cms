module namespace resources = "http://cms.algorythm.de/web/resources";
declare default element namespace "http://cms.algorythm.de/common/CMS";

declare
%rest:GET
%rest:path("resources/{$path=.+}")
function resources:get($path as xs:string) {
	let $absPath := '/home/max/development/java/basex/basex-api/src/main/webapp/repo/http-cms.algorythm.de-cms-api-0.0.1/cms-api/'||$path
	let $mimeType := web:content-type($absPath)
	return (
		<rest:response>
			<output:serialization-parameters>
				<output:media-type value='{$mimeType}'/>
			</output:serialization-parameters>
		</rest:response>,
		(: response:stream-binary(file:read-binary($absPath), 'media-type='||$mimeType, 'file') :)
		file:read-binary($absPath)
	)
};