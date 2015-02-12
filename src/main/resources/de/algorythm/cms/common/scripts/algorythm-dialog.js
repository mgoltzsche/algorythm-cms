(function(angular) {
	var self = {};

	angular.module('algorythm.dialog', []).
	run(['$document', function($document) {
		self.body = $document.find('body');
		self.overlay = angular.element('<div id="dialog-overlay" class="hidden"> </div>');
		self.defaults = {
			cssClass: '',
			template: null,
			templateUrl: null
		};
		
		self.body.append(self.overlay);
	}]).
	factory('createDialog',
			['$document', '$compile', '$rootScope', '$controller', '$window', '$timeout',
			function ($document, $compile, $rootScope, $controller, $window, $timeout) {
		return function Dialog(options, scopeExt) {
			options = angular.extend({}, self.defaults, options);
			var scope = $rootScope.$new(),
				overlay = self.overlay,
				body = self.body,
				window = angular.element($window);
				dialogHeader = angular.element('<div class="dialog-header"><button type="button" class="close" ng-click="$closeDialog()">&times;</button>' + (options.header ? options.header : '') + '</div>'),
				dialogFooter = angular.element('<div class="dialog-footer">' + (options.footer ? options.footer : ' ') + '</div>'),
				dialogContent = angular.element(options.content
						// Template markup string
						? '<div class="dialog-content">' + options.content + '</div>'
						// Template url
						: '<div class="dialog-content" ng-include="\'' + options.content + '\'"></div>'),
				dialog = angular.element('<div id="dialog" class="' + options.cssClass + '"> </div>'),
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
			
			scope.$resizeDialog = function() {
				// Recalculate dialog size
				$timeout(function() {
					var maxDialogWidth = $window.innerWidth - 40,
						maxDialogHeight = $window.innerHeight - 20,
						dialogElement = dialog[0];
					
					dialog.css('max-width', maxDialogWidth + 'px');
					dialog.css('left', '-1920px');
					dialog.css('top', '-1080px');
					dialog.css('width', 'auto');
					dialog.css('height', 'auto');
					dialogContent.css('height', 'auto');
					dialogContent.removeClass('overflow-horizontal');
				
					var dialogWidth = dialogElement.offsetWidth,
						dialogHeight = dialogElement.offsetHeight,
						headerFooterHeight = dialogHeight - dialogContent[0].offsetHeight,
						maxContentHeight = maxDialogHeight - headerFooterHeight,
						newDialogWidth = dialogWidth > maxDialogWidth ? maxDialogWidth : dialogWidth,
						newDialogHeight = 0;
					
					if (dialogHeight > maxDialogHeight) {
						dialogContent.css('height', maxContentHeight + 'px');
						dialogContent.addClass('overflow-horizontal');
						
						newDialogHeight = maxDialogHeight;
					} else {
						dialogContent.css('height', (dialogHeight - headerFooterHeight) + 'px');
						
						newDialogHeight = dialogHeight;
					}
					
					dialog.css('left', bounds.left + 'px');
					dialog.css('top', bounds.top + 'px');
					dialog.css('width', bounds.width + 'px');
					dialog.css('height', bounds.height + 'px');
					
					if (newDialogWidth != bounds.width || newDialogHeight != bounds.height) {
						//dialog.addClass('resizing');
						
						bounds.width = newDialogWidth;
						bounds.height = newDialogHeight;
						bounds.left = Math.floor($window.innerWidth/2 - newDialogWidth/2);
						bounds.top = Math.floor($window.innerHeight/2 - newDialogHeight/2);
						
						$timeout(function() {
							dialog.css('left', bounds.left + 'px');
							dialog.css('top', bounds.top + 'px');
							dialog.css('width', bounds.width + 'px');
							dialog.css('height', bounds.height + 'px');
							
							/*$timeout(function() {
								dialog.removeClass('resizing');
							}, 500);*/
						});
					} else {
						dialog.css('left', Math.floor($window.innerWidth/2 - newDialogWidth/2) + 'px');
						dialog.css('top', Math.floor($window.innerHeight/2 - newDialogHeight/2) + 'px');
					}
				});
			};
			
			dialog.append(dialogHeader).append(dialogContent).append(dialogFooter);
			angular.extend(scope, scopeExt || {});
			
			scope.$closeDialog = function () {
				window.off('resize', scope.$resizeDialog);
				body.off('keydown', onEscPressed);
				overlay.off('click', scope.$closeDialog);
				overlay.addClass('hidden');
				dialog.remove();
				scope.$destroy();
			};
			
			if (options.controller) {
				var ctrl = $controller(options.controller, {$scope: scope});
				
				dialog.contents().data('$ngControllerController', ctrl);
			}
			
			body.on('keydown', onEscPressed);
			overlay.on('click', scope.$closeDialog);
			$compile(dialog)(scope);
			overlay.removeClass('hidden');
			dialog.css('height', 0);
			dialog.css('width', 0);
			body.append(dialog);
			window.on('resize', scope.$resizeDialog);
			scope.$resizeDialog();
		};
	}]);
})(angular);