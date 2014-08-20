/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private scope: portal.IPortalScope
        private timeout: ng.ITimeoutService
        private state: ng.ui.IStateService
        private growl: any
        
        public ready: boolean = false
        public configService: portal.ConfigService
        public methodsService: portal.MethodsService
      
        static $inject: Array<string> = ['$scope', '$timeout', 'configService', 'methodsService', '$state', 'growl']

        constructor($scope: IPortalScope, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            methodsService: portal.MethodsService, $state: ng.ui.IStateService, growl: any) { 
            this.scope = $scope
            this.scope.vm = this
            this.timeout = $timeout
            this.configService = configService
            this.methodsService = methodsService
            this.state = $state
            this.growl = growl
            
            this.timeout(() => { 
                  this.ready = true 
                  this.growl.warning('Configuration loaded, waiting for isAlive...')
            })
            
            //console.log(this.configService.filterRegions)
            
            
            this.scope.$on('service-loading', (e, id: string) => { 
                console.log('loading at '+id)
            })
            
            this.scope.$on('service-success', (e, id: string) => {
                console.log('success at '+id)
            })
            
            this.scope.$on('service-error', (e, id: string) => { 
                console.log('error at '+id)
            })
        }


    }
}