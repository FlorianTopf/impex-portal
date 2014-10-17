/// <reference path='_all.ts' />

module portal {
    'use strict';

    var impexPortal: ng.IModule = angular.module('portal', ['ui.bootstrap', 'ui.bootstrap.datetimepicker', 'ui.router', 
        'ngResource', 'ngStorage', 'angularFileUpload', 'angular-growl', 'rt.encodeuri', 'reCAPTCHA'])
    

    impexPortal.service('configService', ConfigService)
    impexPortal.service('registryService', RegistryService)
    impexPortal.service('methodsService', MethodsService)
    impexPortal.service('userService', UserService)
    impexPortal.service('sampService', SampService)

    impexPortal.controller('configCtrl', ConfigCtrl)
    impexPortal.controller('portalCtrl', PortalCtrl)
    impexPortal.controller('registryCtrl', RegistryCtrl)
    impexPortal.controller('methodsCtrl', MethodsCtrl)
    impexPortal.controller('userDataCtrl', UserDataCtrl)

    impexPortal.directive('databasesDir', DatabasesDir.prototype.injection())
    impexPortal.directive('registryDir', RegistryDir.prototype.injection())
    impexPortal.directive('userDataDir', UserDataDir.prototype.injection())
    impexPortal.directive('selectionDir', SelectionDir.prototype.injection())
    // is in SelectionDir.ts
    impexPortal.directive('memberDir', MemberDir.prototype.injection())
    impexPortal.directive('methodsDir', MethodsDir.prototype.injection())
    impexPortal.directive('canvasDir', CanvasDir.prototype.injection())
           
   
    var routes = impexPortal.config(['$stateProvider', '$urlRouterProvider', 
        ($stateProvider: ng.ui.IStateProvider, $urlRouterProvider: ng.ui.IUrlRouterProvider) => {
        
        $urlRouterProvider.otherwise('/portal')
        
        $stateProvider.state(new App()).
            state(new Portal()).
                state(new Registry()).
                state(new Methods()).
                state(new MyData()).
            state(new Databases()).
            state(new Feedback())
                 
    }])
    
    // global tooltip config
    impexPortal.config(['$tooltipProvider', ($tooltipProvider) => {
            $tooltipProvider.options({
                'placement': 'bottom',
                'animation': 'false',
                'popupDelay': '0',
                'appendToBody': 'true'})
    }])
    
    // global growl config
    impexPortal.config(['growlProvider', (growlProvider) => {
        growlProvider.globalTimeToLive(5000)
        growlProvider.onlyUniqueMessages(false)
        growlProvider.globalPosition('top-right')
    }])
    
    impexPortal.config(['reCAPTCHAProvider', (reCAPTCHAProvider) => {
        // required, please use your own key :)
        reCAPTCHAProvider.setPublicKey('6LcWC_sSAAAAAP6nV13f9yYzGvH-BuFXsJwV_PLy')
        // optional
        reCAPTCHAProvider.setOptions({
            theme: 'clean'
         })
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
            
               //if($window.confirm('connection timed out. retry?'))
               //     $state.transitionTo(toState, toParams)
        })  
    }])
    
    
     
}