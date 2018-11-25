module namespace exmpl = "http://example.org/hello";

declare function exmpl:hello($name as string()) {
	return 'Hello ' || $name
};