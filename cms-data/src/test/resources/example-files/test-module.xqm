module namespace c = 'http://algorythm.de/cms/example/Call';

import module namespace m = 'http://algorythm.de/cms/example/Hello';

declare function c:callLibrary() {
	m:hello('from custom package')
};
