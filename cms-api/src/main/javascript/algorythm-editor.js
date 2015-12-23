(function(require) {
	var angular = require('angular');
	var Medium = require('medium-editor');

	angular.module('algorythm.editor', []).
	directive('editPageTitle', ['$http', function($http) {
		return {
			restrict: 'A',
			link: function(scope, el, attrs) {
				var srcPath = attrs.editPageTitle;
				var colonPos = srcPath.indexOf(':');
				var docPath = srcPath.substring(0, colonPos);
				var xpath = srcPath.substring(colonPos + 1, srcPath.length);
				var state = {
					lastValueSent: el.text()
				};
				
				el.attr('contenteditable', true);
				el.on('keyup', function(evt) {
					var value = el.text();
					
					if (state.lastValueSent != value) {
						$http({
							method: 'GET',
							url: '../update/value' + docPath + '?xpath=' + encodeURIComponent(xpath) + '&value=' + encodeURIComponent(value)
						}).then(function() {
							console.log('updated');
						}, function() {
							console.log('update error');
						});
						state.lastValueSent = value;
					}
				});
			}
		};
	}]).
	directive('editRichText', ['$http', function($http) {
		return {
			restrict: 'A',
			link: function(scope, el, attrs) {
				var contentElement = el[0],
					container = contentElement.parentNode,
					medium = new Medium({
						element: contentElement,
						mode: Medium.richMode,
						placeholder: 'Your Article',
						attributes: null,
						tags: null,
						pasteAsText: false,
						beforeInvokeElement: function () {
							console.log(Medium.Element);
						},
						beforeInsertHtml: function () {
							console.log(Medium.Html);
						},
						beforeAddTag: function (tag, shouldFocus, isEditable, afterElement) {
							console.log(tag);
						}
					});
			}
		};
	}]);
})(require);