/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IRegistryScope extends ng.IScope {
        regvm: RegistryCtrl;
    }

    export class RegistryCtrl {
        private scope: portal.IRegistryScope
        private http: ng.IHttpService
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private registryService: portal.RegistryService
        private modalInstance: any
        private database: Database
        private registryPromise: ng.IPromise<any>
        private initialising: boolean = false
        private loading: boolean = false
        private transFinished: boolean = true
        
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout', 
            'configService', 'registryService', '$modalInstance', 'database']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IRegistryScope, $http: ng.IHttpService, $location: ng.ILocationService,
            $timeout: ng.ITimeoutService, configService: portal.ConfigService, registryService: portal.RegistryService,
            $modalInstance: any, database: Database) {   
            this.scope = $scope
            $scope.regvm = this
            this.configService = configService
            this.registryService = registryService
            this.location = $location
            this.http = $http
            this.timeout = $timeout   
            this.modalInstance = $modalInstance
            this.database = database
            
            // watches changes of variable 
            //(is changed each time modal is opened)
            this.scope.$watch('this.database', 
                () => { this.getRepository(database.id) })
        }
        
        public getRepository(id: string) {
            this.scope.$broadcast('clear-registry')
            this.initialising = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            
            var cacheId = "repo-"+id
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.getRepository().get(
                    { fmt: 'json' , id: id }).$promise
                
                this.registryPromise.then((res) => {
                    var result = <ISpase>res.spase
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.repository)            
                    this.scope.$broadcast('update-repositories', cacheId)
                    this.initialising = false
                })
            } else {
                this.scope.$broadcast('update-repositories', cacheId)
                this.initialising = false
            }
        }
        
        public getSimulationModel(id: string) {
            this.scope.$broadcast('clear-simulation-models')
            this.loading = true
            
            var cacheId = "model-"+id
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.getSimulationModel().get(
                    { fmt: 'json', id: id }).$promise
                
                this.registryPromise.then((res) => {
                    var result = <ISpase>res.spase
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.simulationModel)
                    this.scope.$broadcast('update-simulation-models', cacheId)
                    this.loading = false
                })
            } else {
                this.scope.$broadcast('update-simulation-models', cacheId)
                this.loading = false
            }
        }
        
        public getSimulationRun(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-simulation-runs', element)
            this.loading = true
            
            var cacheId = "run-"+id
            
            if(!(cacheId in this.registryService.cachedElements)) {  
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
                    this.scope.$broadcast('registry-error', 'no simulation run found')
                    this.loading = false 
                })
            } else {
                this.scope.$broadcast('update-simulation-runs', cacheId)
                this.loading = false
            }
        }
        
        public getNumericalOutput(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-numerical-outputs', element)
            this.loading = true
            
            var cacheId = "output-"+id
            
            if(!(cacheId in this.registryService.cachedElements)) {  
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
                    this.scope.$broadcast('registry-error', 'no numerical output found') 
                    this.loading = false 
                })
            } else {
                this.scope.$broadcast('update-numerical-outputs', cacheId)
                this.loading = false   
            }      
        }
        
        public getGranule(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-granules', element)
            this.loading = true
            
            var cacheId = "granule-"+id
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.getGranule().get(
                    { fmt: 'json', id: id }).$promise
                
                this.registryPromise.then(
                (res) => {
                    var result = <ISpase>res.spase
                    this.registryService.cachedElements[cacheId] = result.resources.map((r) => r.granule)
                    this.loading = false
                    this.scope.$broadcast('update-granules', cacheId)
                }, 
                (err) => { 
                    this.scope.$broadcast('registry-error', 'no granule found') 
                    this.loading = false 
                })
            } else {
                this.scope.$broadcast('update-granules', cacheId)
                this.loading = false   
            }  
        }
        
        // testing methods for modal
        public registryOk() {
            this.modalInstance.close()
            // @TODO just for the moment
            this.scope.$broadcast('clear-registry')
        }
        
        public registryCancel() {
            this.modalInstance.dismiss()
            // @TODO just for the moment
            this.scope.$broadcast('clear-registry')
        }
        
    

    }
}