/// <reference path='_all.ts' />

module portal {
    'use strict';

    var impexPortal = angular.module('portal', [,'ui.bootstrap', 'ngRoute', 'ngResource'])
 
    impexPortal.service('configService', ConfigService)
    impexPortal.service('registryService', RegistryService)

    impexPortal.controller('configCtrl', ConfigCtrl)
    impexPortal.controller('portalCtrl', PortalCtrl)

    impexPortal.directive('databasesDir', DatabasesDir.prototype.injection())
    impexPortal.directive('registryDir', RegistryDir.prototype.injection())

    impexPortal.config(['$routeProvider', function($routeProvider) {
		    $routeProvider.when('/config', {templateUrl: '/public/partials/config.html', controller: 'configCtrl'}).
                when('/portal', {templateUrl: '/public/partials/portal.html', controller: 'portalCtrl'}).
                otherwise({redirectTo: '/config'})
  		}])

    impexPortal.run(['$rootScope', '$window', function($rootScope, $window){
        $rootScope.windowWidth = $window.outerWidth
        angular.element($window).bind('resize',function(){
            $rootScope.windowWidth = $window.outerWidth
            $rootScope.$apply('windowWidth')
        })
    }])
     
}