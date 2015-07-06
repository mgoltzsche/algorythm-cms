module namespace m = 'http://algorythm.de/cms/example/Hello';

declare function m:hello($world as xs:string) {
	'Hello ' || $world || '!'
};
