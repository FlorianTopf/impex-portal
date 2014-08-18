/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private scope: portal.IPortalScope
        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private state: ng.ui.IStateService
        private growl: any
        
        public ready: boolean = false
        
      
        static $inject: Array<string> = ['$scope', '$timeout', 'configService', '$state', 'growl']

        constructor($scope: IPortalScope, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            $state: ng.ui.IStateService, growl: any) { 
            this.scope = $scope
            this.scope.vm = this
            this.timeout = $timeout
            this.configService = configService
            this.state = $state
            this.growl = growl
            
            this.timeout(() => { 
                  this.ready = true 
                  this.growl.warning('Configuration loaded, waiting for isAlive...')
            })
          
        }


    }
}