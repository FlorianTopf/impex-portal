/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private scope: portal.IPortalScope
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private interval: ng.IIntervalService
        private window: ng.IWindowService
        private configService: portal.ConfigService
        private config: IConfig
        private registryService: portal.RegistryService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        private modal: any

        public ready: boolean = false
        public status: string
        public showError: boolean = false  
      
        static $inject: Array<string> = ['$scope', '$location', '$timeout', '$interval', 
            '$window', 'configService', 'registryService', 'userService', '$state', '$modal']

        constructor($scope: IPortalScope, $location: ng.ILocationService, $timeout: ng.ITimeoutService, 
            $interval: ng.IIntervalService, $window: ng.IWindowService, configService: portal.ConfigService, 
            registryService: portal.RegistryService, userService: portal.UserService, 
            $state: ng.ui.IStateService, $modal) { 
            this.scope = $scope
            this.scope.vm = this
            this.location = $location
            this.timeout = $timeout
            this.interval = $interval
            this.window = $window
            this.configService = configService
            this.config = this.configService.config
            this.registryService = registryService
            this.userService = userService
            this.state = $state
            this.modal = $modal
            

            this.timeout(() => { 
                  this.ready = true 
            })
            
            
        }


    }
}