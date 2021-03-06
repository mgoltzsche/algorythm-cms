(function(require) {
	var angular = require('angular');
	require('angular-animate');
	
	var self = {};

	angular.module('algorythm.dialog', ['ngAnimate']).
	run(['$document', function($document) {
		self.body = $document.find('body');
		self.overlay = angular.element('<div id="dialog-overlay" class="hidden"> </div>');
		self.defaults = {
			cssClass: 'animate-slidevertical',
			template: null,
			templateUrl: null,
			resizeProportional: false
		};
		
		self.body.append(self.overlay);
	}]).
	factory('createDialog',
			['$document', '$compile', '$rootScope', '$controller', '$window', '$timeout', '$animate',
			function ($document, $compile, $rootScope, $controller, $window, $timeout, $animate) {
		return function Dialog(options, scopeExt) {
			options = angular.extend({}, self.defaults, options);
			var scope = $rootScope.$new(),
				overlay = self.overlay,
				body = self.body,
				window = angular.element($window),
				dialogHeader = angular.element('<div class="dialog-header"><span class="btn close" ng-click="$closeDialog()">&times;</span>' + (options.header ? options.header : '') + '</div>'),
				dialogFooter = angular.element('<div class="dialog-footer">' + (options.footer ? options.footer : ' ') + '</div>'),
				dialogContent = angular.element(options.content ?
						// Template markup string
						'<div class="dialog-content">' + options.content + '</div>'
						// Template url
						: '<div class="dialog-content" ng-include="\'' + options.content + '\'"></div>'),
				dialog = angular.element('<div class="dialog dialog-initialization ' + options.cssClass + '"> </div>'),
				dialogContainer = angular.element('<div class="dialog-container"> </div>'),
				onBackgroundClick = function() {
					scope.$apply(function() {
						scope.$closeDialog();
					});
				},
				onEscPressed = function(event) {
					if (event.keyCode === 27) {
						scope.$closeDialog();
					}
				},
				bounds = {
					width: 0,
					height: 0,
					left: 0,
					top: -$window.innerHeight
				};
			
			dialogContainer.css('left', '20px');
			dialogContainer.css('top', '20px');
			
			scope.$resizeDialog = function() {
				var lastDialogContentHeight = dialogContent[0].offsetHeight;
				
				// Recalculate dialog size
				$timeout(function() {
					var maxDialogWidth = $window.innerWidth - 40,
						maxDialogHeight = $window.innerHeight - 20,
						dialogElement = dialog[0],
						dialogContainerElement = dialogContainer[0],
						dialogContentElement = dialogContent[0];
					
					dialogContainer
						.css('position', 'fixed')
						.css('max-width', maxDialogWidth + 'px');
					
					dialogContent.css('width', 'auto').css('height', 'auto');
					
					var preferredDialogHeight = dialogContainerElement.offsetHeight,
						preferredContentHeight = dialogContentElement.offsetHeight,
						preferredContentWidth = dialogContentElement.offsetWidth,
						newDialogWidth = 0,
						newDialogHeight = preferredDialogHeight > maxDialogHeight ? maxDialogHeight : preferredDialogHeight,
						headerFooterHeight = preferredDialogHeight - preferredContentHeight,
						newContentHeight = newDialogHeight - headerFooterHeight;
					
					dialogContent.css('height', newContentHeight + 'px');
					
					if (options.resizeProportional) { // Fixes dialog scaling when working with images in Chrome
						var contentScale = newContentHeight / preferredContentHeight,
							newContentWidth = Math.ceil(preferredContentWidth * contentScale);
						
						dialogContent.css('width', newContentWidth + 'px');
					}
					
					newDialogWidth = dialogContainerElement.offsetWidth;
					
					dialogContainer.css('width', newDialogWidth + 'px');
					
					newDialogHeight = dialogContainerElement.offsetHeight;
					
					dialogContent.css('width', 'auto');
					dialogContainer.css('width', 'auto');
					dialogContainer.css('position', 'static');
					
					bounds.left = Math.floor($window.innerWidth/2 - newDialogWidth/2);
					bounds.top = Math.floor($window.innerHeight/2 - newDialogHeight/2);
					
					if (newDialogWidth != bounds.width || newDialogHeight != bounds.height) {
						bounds.width = newDialogWidth;
						bounds.height = newDialogHeight;
						
						dialog
							.css('width', bounds.width + 'px')
							.css('height', bounds.height + 'px');
					}
					
					dialog
						.css('left', bounds.left + 'px')
						.css('top', bounds.top + 'px');
				});
			};
			
			dialogContainer.append(dialogHeader).append(dialogContent).append(dialogFooter);
			dialog.append(dialogContainer);
			angular.extend(scope, scopeExt || {});
			
			scope.$closeDialog = function () {
				window.off('resize', scope.$resizeDialog);
				body.off('keydown', onEscPressed);
				overlay.off('click', scope.$closeDialog);
				overlay.addClass('hidden');
				scope.$destroy();
				$animate.leave(dialog);
			};
			
			if (options.controller) {
				var ctrl = $controller(options.controller, {$scope: scope});
				
				dialog.contents().data('$ngControllerController', ctrl);
			}
			
			overlay.on('click', onBackgroundClick);
			body.on('keydown', onEscPressed);
			$compile(dialog)(scope);
			overlay.removeClass('hidden');
			dialog.css('height', 0);
			dialog.css('width', 0);
			var children = body.children();
			$animate.enter(dialog, body, children[children.length - 1]);
			scope.$resizeDialog();
			window.on('resize', scope.$resizeDialog);
		};
	}]);
})(require);