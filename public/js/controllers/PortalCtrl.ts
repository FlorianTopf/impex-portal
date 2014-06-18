/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private config: IConfig
        private configService: portal.ConfigService
        private registryService: portal.RegistryService

        private http: ng.IHttpService
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private interval: ng.IIntervalService
        private window: ng.IWindowService
        private scope: portal.IPortalScope
        private modal: any
        private configPromise: ng.IPromise<any>
        private registryPromise: ng.IPromise<any>

        private configAble: boolean = true
        private ready: boolean = false
        private loading: boolean = false
        private initialising: boolean = false
        private transFinished: boolean = true
 
      
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout',
            '$interval','$window', 'configService', 'registryService', '$modal']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IPortalScope, $http: ng.IHttpService, $location: ng.ILocationService, 
            $timeout: ng.ITimeoutService, $interval: ng.IIntervalService,
            $window: ng.IWindowService, configService: portal.ConfigService, registryService: portal.RegistryService, $modal) { 
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
        
        public getRepository(id: string) {
            this.scope.$broadcast('clear-registry')
            this.initialising = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            
            var cacheId = "repo-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                console.log("Not-Cached="+cacheId)
                this.registryPromise = this.registryService.getRepository().get(
                    { fmt: 'json' , id: id }).$promise
                
                this.registryPromise.then((res) => {
                    var result = <ISpase>res.spase
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.repository)            
                    this.scope.$broadcast('update-repositories', cacheId)
                    this.initialising = false
                })
            } else {
                console.log("Cached="+cacheId)
                this.scope.$broadcast('update-repositories', cacheId)
                this.initialising = false
            }
        }
        
        public getSimulationModel(id: string) {
            this.scope.$broadcast('clear-simulation-models')
            this.loading = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            
            var cacheId = "model-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                console.log("Not-Cached="+cacheId)
                this.registryPromise = this.registryService.getSimulationModel().get(
                    { fmt: 'json', id: id }).$promise
                
                this.registryPromise.then((res) => {
                    var result = <ISpase>res.spase
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.simulationModel)
                    this.scope.$broadcast('update-simulation-models', cacheId)
                    this.loading = false
                })
            } else {
                console.log("Cached="+cacheId)
                this.scope.$broadcast('update-simulation-models', cacheId)
                this.loading = false
            }
        }
        
        public getSimulationRun(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-simulation-runs', element)
            this.loading = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            
            var cacheId = "run-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                console.log("Not-Cached="+cacheId)
                this.registryPromise = this.registryService.getSimulationRun().get(
                    { fmt: 'json', id: id }).$promise
                
                this.registryPromise.then(
                (res) => {
                    var result = <ISpase>res.spase
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.simulationRun)
                    this.loading = false
                    this.scope.$broadcast('update-simulation-runs', cacheId)
                }, 
                (err) => { 
                    this.scope.$broadcast('registry-error', 'no simulation run')
                    this.loading = false 
                })
            } else {
                console.log("Cached="+cacheId)
                this.scope.$broadcast('update-simulation-runs', cacheId)
                this.loading = false
            }
        }
        
        public getNumericalOutput(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-numerical-outputs', element)
            this.loading = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            
            var cacheId = "output-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                console.log("Not-Cached="+cacheId)
                this.registryPromise = this.registryService.getNumericalOutput().get(
                    { fmt: 'json', id: id }).$promise
                
                this.registryPromise.then(
                (res) => {
                    var result = <ISpase>res.spase
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.numericalOutput)
                    this.loading = false
                    this.scope.$broadcast('update-numerical-outputs', cacheId)
                }, 
                (err) => { 
                    this.scope.$broadcast('registry-error', 'no numerical output') 
                    this.loading = false 
                })
            } else {
                console.log("Cached="+cacheId)
                this.scope.$broadcast('update-numerical-outputs', cacheId)
                this.loading = false   
            }      
        }
        
        public getGranule(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-granules', element)
            this.loading = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            var cacheId = "granule-"+id
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                console.log("Not-Cached="+cacheId)
                this.registryPromise = this.registryService.getGranule().get(
                    { fmt: 'json', id: id }).$promise
                
                this.registryPromise.then(
                (res) => {
                    var result = <ISpase>res.spase
                    console.log("granule="+result.resources[0].granule.resourceId.split('/').reverse()[0])
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.granule)
                    this.loading = false
                    this.scope.$broadcast('update-granules', cacheId)
                }, 
                (err) => { 
                    this.scope.$broadcast('registry-error', 'no granule') 
                    this.loading = false 
                })
            } else {
                console.log("Cached="+cacheId)
                this.scope.$broadcast('update-granules', cacheId)
                this.loading = false   
            }  
        }
        
        public open(size: string) {
            
            var modalInstance = this.modal.open({
                    templateUrl: '/public/partials/templates/modal.html',
                    controller: ModalCtrl,
                    size: size
            })
            
            modalInstance.result.then(
            (ok) => console.log(ok), 
            (cancel) => console.log(cancel))
       
        }
        

    }
}
