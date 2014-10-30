angular.module('cms', ['ngRoute']).config(['$routeProvider', function($routeProvider) {
						$routeProvider.when('/:name', {
							templateUrl: function(p) {
								alert(window.location);
								return '/' + p.name + '/content.html';
							}//,
//							controller: 'CMSController'
						});
					}]);