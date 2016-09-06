module namespace m = 'http://algorythm.de/cms/example/Hello';

declare function m:hello($world) {
	'Hello ' || $world || '!'
};