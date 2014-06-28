/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private scope: portal.IPortalScope
        private http: ng.IHttpService
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private interval: ng.IIntervalService
        private window: ng.IWindowService
        private config: IConfig
        private configService: portal.ConfigService
        private registryService: portal.RegistryService
        private modal: any
        private registryPromise: ng.IPromise<any>

        private configAble: boolean = true
        private ready: boolean = false
      
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout', 
            '$interval', '$window', 'configService', 'registryService', '$modal']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IPortalScope, $http: ng.IHttpService, $location: ng.ILocationService, 
            $timeout: ng.ITimeoutService, $interval: ng.IIntervalService, $window: ng.IWindowService, 
            configService: portal.ConfigService, registryService: portal.RegistryService, $modal) { 
            this.scope = $scope
            this.scope.vm = this
            this.http = $http
            this.location = $location
            this.configService = configService
            this.config = this.configService.config
            this.registryService = registryService
            this.timeout = $timeout
            this.interval = $interval
            this.window = $window
            this.modal = $modal
            
            if(this.config == null) {
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
