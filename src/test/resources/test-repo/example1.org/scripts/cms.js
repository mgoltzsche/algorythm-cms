(function(w) {
var absoluteUrlRegex = /^(\/|[\w]+:\/\/).+/;
var parentUrlRegex = /^(.*?)\/?[^\/#?]*([#?].*)?$/;
var urlBackRegex = /\/[^\/]+\/\.\./g
var urlThisRegex = /\/\./g
var startFileRegex = /^(.*?)(\/|\/index\.html)?[^\/#?]*$/;

var parentUrl = function(url) {
	return parentUrlRegex.exec(url)[1];
};

var normalizedUrl = function(url) {
	do {
		lastUrl = url;
		url = url.replace(urlBackRegex, ""); 
	} while (url != lastUrl);
	
	do {
		lastUrl = url;
		url = url.replace(urlThisRegex, ""); 
	} while (url != lastUrl);
	
	return url;
};

String.prototype.toAbsoluteUrl = function() {
	if (absoluteUrlRegex.test(this))
		return normalizedUrl(this);
	else
		return normalizedUrl(parentUrl(w.location.href) + '/' + this);
};

if (cms.baseUrl != '.' || !startFileRegex.test(w.location.href)) {
	var absBaseUrl = cms.baseUrl.toAbsoluteUrl();
	var absUrl = w.location.href;
	var anchor = parentUrl(absUrl.substring(absBaseUrl.length, absUrl.length));
	w.location.href = absBaseUrl + '/#' + anchor;
}
})(window);


var obj2str = function(o) {
	var s = "";
	for (k in o) {
		s += k + " = " + (typeof o[k] == 'function' ? 'function' : o[k]) + "\n";
	}
	return s;
};

angular.module('cms', ['ngRoute']).
		config(['$routeProvider', function($routeProvider) {
	var anchorRegex = /#(\/.+?)\/?$/;
	var pathRegex = /^[\w]+:\/\/[^\/]*(\/.*?\/)([^\/]+)?(#.*)?$/
	var url = window.location;
	var match = pathRegex.exec(url);
	//var basePath = match ? match[1] : '/';
	//alert(basePath);
	 
	$routeProvider.otherwise({
		templateUrl: function() {
			var match = anchorRegex.exec(window.location);
			
			return cms.baseUrl + (match ? match[1] : '') + '/content.html';
		}/*,
		controller: function($rootScope) {
			//alert('## ' + $('main'));
			$rootScope.title = 'test';
			//$('title').text('test');
		}*/
	});
}]).
directive('a', ['$location', function($location) {
	return {
		restrict: 'E',
		link: function(scope, el, attrs) {
			var url = attrs.href;
			
			if (url.indexOf(cms.baseUrl) == 0 && url.indexOf('/index.html') == url.length - 11) {
				// Rewrite internal URL to AJAX call
				url = url.substring(cms.baseUrl.length, url.length - 11);
				
				el.attr('href', '#' + (url ? url : '/'));
			}
		}
	};
}]).
directive('cmsPageTitle', ['$rootScope', function($rootScope) {
	return {
		restrict: 'A',
		link: function(scope, el, attrs) {
			$rootScope.pageTitle = attrs.cmsPageTitle;
		}
	};
}]).
directive('cmsMenu', ['$location', function($location) {
	return {
		restrict: 'A',
		link: function(scope, el, attrs) {
			var clazz = attrs.cmsMenu ? attrs.cmsMenu : 'selected';
			
			scope.$on('$locationChangeStart', function() {
				var currentPath = $location.path();
				var links = el.find('a');
				
				for (var i = 0; i < links.length; i++) {
					var a = angular.element(links[i]);
					var path = a.attr('href').substring(1);
					var li = a.parent();
					
					if (path === currentPath || currentPath.indexOf(path + '/') == 0) {
						li.addClass(clazz);
					} else {
						li.removeClass(clazz);
					}
				}
			});
		}
	};
}]);