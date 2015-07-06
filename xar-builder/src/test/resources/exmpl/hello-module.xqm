module namespace exmpl = "http://example.org/hello";

declare function exmpl:hello($name) {
	'Hello ' || $name
};

declare function exmpl:asText($xml as item()) {
	xslt:transform($xml, 'http://example.org/xml2txt.xsl')
};