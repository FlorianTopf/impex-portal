/// <reference path='_all.ts' />

module portal {
    'use strict';

    var impexPortal: ng.IModule = angular.module('portal', ['ui.bootstrap', 'ui.router', 'ngResource', 'ngStorage'])
    
 
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
    impexPortal.directive('selectionDir', SelectionDir.prototype.injection())
    // is in SelectionDir.ts
    impexPortal.directive('memberDir', MemberDir.prototype.injection())
    
    // simple directives for displaying JSON objects
    /*impexPortal.directive('selection', () => {
        return {
            restrict: "E",
            replace: true,
            scope: {
                selection: '='
            },
            template: "<ul>"+
                "<member ng-repeat='(key, elem) in selection' name='key' member='elem' "+
                "ng-if='!(elem | isEmpty)'></member>"+
                "</ul>"
        }
    })*/
    
    /*impexPortal.directive('member', ($compile) => {
        function beautify(str: string): string {
            var array = str.match(/([A-Z]?[^A-Z]*)/g).slice(0,-1)
            var first = array[0].charAt(0).toUpperCase()+array[0].slice(1)
            array.shift()
            return (first+" "+array.join(" ")).trim()
        }
        
        function validateUrl(str: string): boolean {
            var pattern = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/
            if(!pattern.test(str)) {
                return false
            } else {
                return true
            }
        }
        
        return {
            restrict: "E",
            replace: true,
            scope: {
                name: '=',
                member: '=',
            },
            template: "<li></li>",
            link: ($scope, element, attributes) => { 
                element.append("<strong>"+beautify($scope.name)+"</strong>: ")
                                 
                if(angular.isArray($scope.member)) {
                    angular.forEach($scope.member, (m, i) => {
                        if(angular.isString(m) || angular.isNumber(m))
                            element.append(m+" ")
                        else if(angular.isObject(m)) {
                            $compile("<br/><selection-dir selection='"+JSON.stringify($scope.member[i])+"'></selection-dir>")
                                ($scope, (cloned, $scope) => { element.append(cloned) })
                        } 
                    })
                    
                } else if(angular.isObject($scope.member)) {
                    element.append("<br/><selection-dir selection='member'></selection-dir>")
                    $compile(element.contents())($scope)
                } else if(validateUrl($scope.member)) {
                    element.append("<a href='"+$scope.member+"' target='_blank'>"+$scope.member+"</a><br/>")
                } else {
                    element.append($scope.member+"<br/>")
                }

            }
            
        }
    })*/
     
   
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