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
        private registryService: portal.RegistryService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        private modal: any

        public ready: boolean = false
      
        static $inject: Array<string> = ['$scope', '$timeout', 'configService', '$state', '$modal']

        constructor($scope: IPortalScope, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            $state: ng.ui.IStateService, $modal: any) { 
            this.scope = $scope
            this.scope.vm = this
            this.timeout = $timeout
            this.configService = configService
            this.state = $state
            this.modal = $modal
            
            this.timeout(() => { 
                  this.ready = true 
            })
            
            
        }


    }
}