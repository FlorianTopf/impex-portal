/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IRegistryDirScope extends ng.IScope {
       registryvm: RegistryDir
    }

    export class RegistryDir {

        // @TODO here we add dependencies for the used elements in bootstrap-ui
        public injection(): any[] {
            return [
                '$timeout',
                'configService',
                'registryService',
                ($timeout: ng.ITimeoutService, configService: portal.ConfigService, 
                    registryService: portal.RegistryService) => { 
                    return new RegistryDir($timeout, configService, registryService); }
            ]
        }
        
        public link: ($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        
        private configService: portal.ConfigService
        private registryService: portal.RegistryService
        private timeout: ng.ITimeoutService
        private myScope: ng.IScope

        constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            registryService: portal.RegistryService) {
            this.configService = configService
            this.registryService = registryService
            this.timeout = $timeout
            this.templateUrl = '/public/partials/templates/registry.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
            
        }

        linkFn($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.registryvm = this
            this.myScope = $scope
        }
        
        
    }

}