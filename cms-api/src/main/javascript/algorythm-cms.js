(function(w, require) {
var angular = require('angular');
require('angular-route');
require('./algorythm-dialog.js');
require('./algorythm-editor.js');

var absoluteUrlRegex = /^(\/|[\w]+:\/\/).+/;
var parentUrlRegex = /^(.*?)\/?[^\/#?]*([#?].*)?$/;
var urlBackRegex = /\/[^\/]+\/\.\./g;
var urlThisRegex = /\/\.(\/|$)/g;
var startFileRegex = /^(.*?)(\/|\/index\.html)?[^\/#?]*$/;

var parentUrl = function(url) {
	return parentUrlRegex.exec(url)[1];
};

var normalizedUrl = function(url) {
	var lastUrl;
	
	do {
		lastUrl = url;
		url = url.replace(urlThisRegex, '$1');
	} while (url != lastUrl);
	
	do {
		lastUrl = url;
		url = url.replace(urlBackRegex, '');
	} while (url != lastUrl);
	
	return url;
};

var absoluteUrl = function(href, base) {
	if (absoluteUrlRegex.test(href))
		return normalizedUrl(href);
	else
		return normalizedUrl(parentUrl(base) + '/' + href);
};

if (!cms.baseUrl) {
	cms.baseUrl = '.';
}

if (cms.baseUrl != '.'/* || !startFileRegex.test(w.location.href)*/) {
	var absUrl = w.location.href;
	var absBaseUrl = absoluteUrl(cms.baseUrl, absUrl);
	var anchor = parentUrl(absUrl.substring(absBaseUrl.length, absUrl.length));
	w.location.href = absBaseUrl + 'index.html#' + anchor;
}

var obj2str = function(o) {
	var s = "";
	for (var k in o) {
		s += k + " = " + (typeof o[k] == 'function' ? 'function' : o[k]) + "\n";
	}
	return s;
};

var absSiteRootUrl = absoluteUrl(cms.baseUrl, w.location.href) + '/';
var images = [];

angular.module('algorythm.cms', ['ngRoute', /*'ngTouch',*/ 'algorythm.dialog', 'algorythm.editor']).
		config(['$routeProvider', function($routeProvider) {
	var anchorRegex = /#(\/.+?)\/?$/,
		pathRegex = /^[\w]+:\/\/[^\/]*(\/.*?\/)([^\/]+)?(#.*)?$/,
		url = window.location,
		match = pathRegex.exec(url);
	
	$routeProvider.otherwise({
		templateUrl: function() {
			images = [];
			var match = anchorRegex.exec(w.location);
			
			return cms.baseUrl + (match ? match[1] : '') + '/content.html';
		},
		controller: ['$scope', '$location', function($scope, $location) {
			var path = $location.path();
			
			$scope.cms.absPageBaseUrl = path == '/' ? absSiteRootUrl : absoluteUrl(path.substring(1), absSiteRootUrl) + '/';
		}]
	});
}]).
run(['$rootScope', function($rootScope) {
	$rootScope.cms = {
		absPageBaseUrl: absSiteRootUrl,
		getImages: function() {
			return images;
		}
	};
}]).
directive('a', ['$location', function($location) {
	return {
		restrict: 'E',
		link: function(scope, el, attrs) {
			var url = attrs.href;
			
			if (url) {
				url = absoluteUrl(url, scope.cms.absPageBaseUrl);
				
				if (url.indexOf(absSiteRootUrl) === 0 && url.indexOf('/index.html') == url.length - 11) {
					// Rewrite internal URL to AJAX call
					url = url.substring(absSiteRootUrl.length - 1, url.length - 11);
					el.attr('href', '#' + (url ? url : '/'));
				}
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
directive('cmsLanguage', ['$location', function($location) {
	return {
		restrict: 'A',
		link: function(scope, el, attrs) {
			var lang = attrs.cmsLanguage;
			
			if (lang) {
				scope.$on('$locationChangeStart', function() {
					el.attr('href', absSiteRootUrl + '../' + lang + '/#' + $location.path());
				});
			}
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
					
					if (path === currentPath ||
							currentPath.indexOf(path + '/') === 0) {
						li.addClass(clazz);
					} else {
						li.removeClass(clazz);
					}
				}
			});
		}
	};
}]).
directive('cmsCollapse', ['$log', '$document', function($log, $document) {
	return {
		restrict: 'A',
		link: function(scope, el, attrs) {
			var id = attrs.cmsCollapse,
				collapsibleElement = document.getElementById(id),
				flagVar = 'collapsed' + id;
			
			if (!collapsibleElement) {
				$log.error("Cannot find collapsible element '" + id + "'");
				return;
			}
			
			scope[flagVar] = true;
			var collapsible = angular.element(collapsibleElement);
			var collapse = function() {
				el.removeClass("active");
				collapsible.addClass("collapsed");
				$document.off('click', docClickListener);
				scope[flagVar] = true;
			},
			contained = function(elem, container) {
				while (elem) {
					if (elem == container)
						return true;
					
					elem = elem.parentNode;
				}
				
				return false;
			},
			docClickListener = function(event) {
				var target = event.target;
				
				if (target != el[0] && (!target.getAttribute('cms-collapse') ||
						!contained(target, collapsibleElement)))
					collapse();
			};
			
			collapsible.addClass("collapsed");
			el.on('click', function() {
				if (scope[flagVar]) {
					el.addClass("active");
					collapsible.removeClass("collapsed");
					$document.on('click', docClickListener);
					scope[flagVar] = false;
				} else {
					collapse();
				}
			});
		}
	};
}]).
directive('cmsBotSafe', function() {
	return {
		restrict: 'A',
		link: function(scope, el, attrs) {
			var content = el.text().trim();
			content = content.replace(/x([0-9A-Fa-f]{2})/g, function(a, b) {
				return String.fromCharCode(parseInt(b, 16));
			});
			el.text(content);
		}
	};
}).
directive('cmsImageDialog', ['createDialog', function(createDialog) {
	var createIcon = function(iconId) {
		return '<svg xmlns:xlink="http://www.w3.org/1999/xlink"><use xlink:href="/sprites.svg#' + iconId + '" /></svg>';
	};
	
	return {
		restrict: 'A',
		link: function(scope, el, attrs) {
			var url = attrs.src,
				txt = attrs.cmsImageDialog,
				images = scope.cms.getImages(),
				imageIndex = images.length,
				imageObj = {url: url, description: txt};
			
			images.push(imageObj);
			
			if (scope.currentIndex === undefined) {
				scope.currentIndex = 0;
			}
			
			el.css('cursor', 'pointer');
			el.on('click', function() {
				createDialog({
					cssClass: 'image-view animate-slidevertical',
					resizeProportional: true,
					content: '<img ng-src="{{imageSrc}}" alt="" ng-swipe-left="previousImage()" ng-swipe-right="nextImage()" />',
					footer: '<span ng-click="previousImage()" ng-show="hasPreviousImage" class="btn previous">&laquo;</span><span ng-click="nextImage()" ng-show="hasNextImage" class="btn next">&raquo;</span><div class="image-description">{{imageDescription}}</div>',
					controller: function($scope, $timeout) {
						var applyImage = function(img) {
							$scope.imageSrc = img.url;
							$scope.imageDescription = img.description;
							$scope.hasPreviousImage = $scope.currentIndex > 0;
							$scope.hasNextImage = $scope.currentIndex < images.length - 1;
						};
						
						$scope.currentIndex = imageIndex;
						
						applyImage(imageObj);
						
						$scope.previousImage = function() {
							if ($scope.hasPreviousImage) {
								applyImage(images[--$scope.currentIndex]);
								$scope.$resizeDialog();
							}
						};
						$scope.nextImage = function() {
							if ($scope.hasNextImage) {
								applyImage(images[++$scope.currentIndex]);
								$scope.$resizeDialog();
							}
						};
					}
				});
			});
		}
	};
}]);
})(window, require);