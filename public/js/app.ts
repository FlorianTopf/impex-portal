/// <reference path='_all.ts' />

module portal {
    'use strict';

    //var impexPortal = angular.module('portal', ['ui.bootstrap', 'ngRoute', 'ngResource'])
    var impexPortal: ng.IModule = angular.module('portal', ['ui.bootstrap', 'ui.router', 'ngResource', 'ngStorage'])
    
    // here we also add options for bootstrap-ui
 
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
   
    /* impexPortal.config(['$routeProvider', ($routeProvider) => {
            $routeProvider.when('/config', {templateUrl: '/public/partials/config.html', controller: 'configCtrl'}).
                when('/portal', {templateUrl: '/public/partials/portalMap.html', controller: 'portalCtrl'}).
                when('/databases', {templateUrl: '/public/partials/databaseMap.html', controller: 'portalCtrl'}).
                otherwise({redirectTo: '/config'})
        }])*/
    impexPortal.config(['$stateProvider', '$urlRouterProvider', 
        ($stateProvider: ng.ui.IStateProvider, $urlRouterProvider: ng.ui.IUrlRouterProvider) => {
        
        $urlRouterProvider.otherwise('/portal')
        
        $stateProvider.state(new App()).
            state(new Portal()).
                state(new Registry()).
                state(new Methods()).
            state(new Databases())
            
            
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
    
    impexPortal.run(['$rootScope', '$state', '$window', ($rootScope, $state, $window) => {
        $rootScope.$on('$stateChangeStart',  (event, toState, toParams, fromState, fromParams) => {
            //console.log("FROM "+JSON.stringify(fromState)+JSON.stringify(fromParams))
            //console.log("TO "+JSON.stringify(toState)+JSON.stringify(toParams))
        })     
        
        // @TODO when we use resolver, we must check errors on promises here!
        $rootScope.$on('$stateChangeError',  (event, toState, toParams, fromState, fromParams, error) => {
               //console.log("Error "+JSON.stringify(error))
                if($window.confirm('connection timed out. retry?'))
                    $state.transitionTo(toState, toParams)
        })  
    }])
    
    
     
}