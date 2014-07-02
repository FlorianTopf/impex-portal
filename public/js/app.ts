/// <reference path='_all.ts' />

module portal {
    'use strict';

    var impexPortal = angular.module('portal', ['ui.bootstrap', 'ngRoute', 'ngResource'])
    
    // here we also add options for bootstrap-ui
    // maybe include angular.ui.router 
    // check what we can directly add to the angular module
    //(see infoday examples, it supports application state, very nice!)
 
    impexPortal.service('configService', ConfigService)
    impexPortal.service('registryService', RegistryService)
    impexPortal.service('methodsService', MethodsService)
    impexPortal.service('userService', UserService)

    impexPortal.controller('configCtrl', ConfigCtrl)
    impexPortal.controller('portalCtrl', PortalCtrl)
    impexPortal.controller('registryCtrl', RegistryCtrl)
    impexPortal.controller('methodsCtrl', MethodsCtrl)

    impexPortal.directive('databasesDir', DatabasesDir.prototype.injection())
    impexPortal.directive('registryDir', RegistryDir.prototype.injection())
    impexPortal.directive('userDataDir', UserDataDir.prototype.injection())
   
    // we can add here other configs too
    impexPortal.config(['$routeProvider', ($routeProvider) => {
		    $routeProvider.when('/config', {templateUrl: '/public/partials/config.html', controller: 'configCtrl'}).
                when('/portal', {templateUrl: '/public/partials/portalMap.html', controller: 'portalCtrl'}).
                when('/databases', {templateUrl: '/public/partials/databaseMap.html', controller: 'portalCtrl'}).
                otherwise({redirectTo: '/config'})
  		}])
    
    // global tooltip config
    impexPortal.config(['$tooltipProvider', ($tooltipProvider) => {
            $tooltipProvider.options({
                'placement': 'bottom',
                'animation': 'false',
                'popupDelay': '0',
                'appendToBody': 'true'})
        }])
    
     // custom filter for checking if an array is empty
    impexPortal.filter('isEmpty', () => {
        var bar
        return (obj) => {
            for (bar in obj) {
                if (obj.hasOwnProperty(bar)) {
                    return false;
                }
            }
            return true;
        }
    })

    impexPortal.run(['$rootScope', '$window', ($rootScope, $window) => {
        $rootScope.windowWidth = $window.outerWidth
        angular.element($window).bind('resize', () => {
            $rootScope.windowWidth = $window.outerWidth
            $rootScope.$apply('windowWidth')
        })
    }])
     
}