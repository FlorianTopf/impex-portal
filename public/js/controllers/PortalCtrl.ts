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
        private registryService: portal.RegistryService
        private userService: portal.UserService
        private modal: any
        private config: IConfig
        private registryPromise: ng.IPromise<any>
        private ready: boolean = false
      
        static $inject: Array<string> = ['$scope', '$location', '$timeout', '$interval', 
            '$window', 'configService', 'registryService', 'userService', '$modal']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IPortalScope, $location: ng.ILocationService, $timeout: ng.ITimeoutService, 
            $interval: ng.IIntervalService, $window: ng.IWindowService, configService: portal.ConfigService, 
            registryService: portal.RegistryService, userService: portal.UserService, $modal) { 
            this.scope = $scope
            this.scope.vm = this
            this.location = $location
            this.timeout = $timeout
            this.interval = $interval
            this.window = $window
            this.configService = configService
            this.registryService = registryService
            this.userService = userService
            this.modal = $modal
            this.config = this.configService.config

            if(this.config == null || this.userService.user == null) {
                $location.path('/config')
            } else {
                 this.timeout(() => { 
                    this.ready = true 
                 })
            }
        }
          
        // testing method for registry modal
        public openRegistryModal(database: Database) {
            var modalInstance = this.modal.open({
                    templateUrl: '/public/partials/templates/registryModal.html',
                    controller: RegistryCtrl,
                    size: 'lg',
                    resolve: {
                        database: () => database 
                    }
            })
            
            modalInstance.result.then(
            (ok) => console.log('ok'), 
            (cancel) => console.log('cancel'))
       
        }
        
        // testing method for methods modal
        public openMethodsModal(database: Database) {
            var modalInstance = this.modal.open({
                    templateUrl: '/public/partials/templates/methodsModal.html',
                    controller: MethodsCtrl,
                    size: 'lg',
                    resolve: {
                        database: () => database 
                    }
            })
            
            modalInstance.result.then(
            (ok) => console.log('ok'), 
            (cancel) => console.log('cancel'))
       
        }        

    }
}
