/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private config: Config
        private configService: portal.ConfigService

        private http: ng.IHttpService
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private interval: ng.IIntervalService
        private window: ng.IWindowService
        private scope: portal.IPortalScope
        
        private configAble: boolean = true

        private ready: boolean = false
      
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout',
            '$interval','$window', 'configService']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IPortalScope, $http: ng.IHttpService, $location: ng.ILocationService, 
            $timeout: ng.ITimeoutService, $interval: ng.IIntervalService,
            $window: ng.IWindowService, configService: portal.ConfigService) { 
            this.scope = $scope
            this.scope.vm = this
            this.http = $http
            this.location = $location
            this.configService = configService
            this.config = this.configService.config
            this.timeout = $timeout
            this.interval = $interval
            this.window = $window
            
            if(this.config == null) {
                $location.path('/config')
            } else {
                 this.timeout(() => { this.ready = true })
            }
        }

        public doSomething() {
            
        }



    }
}
