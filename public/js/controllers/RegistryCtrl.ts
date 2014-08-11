/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IRegistryScope extends ng.IScope {
        regvm: RegistryCtrl
    }

    export class RegistryCtrl {
        private scope: portal.IRegistryScope
        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private registryService: portal.RegistryService
        private state: ng.ui.IStateService
        private modalInstance: any
        private registryPromise: ng.IPromise<ISpase>
        private database: Database
        
        public isFirstOpen: boolean = true
        public initialising: boolean = false
        public loading: boolean = false
        //public transFinished: boolean = true
        
        static $inject: Array<string> = ['$scope', '$timeout', 'configService', 'registryService', 
            '$state', '$modalInstance', 'id']

        constructor($scope: IRegistryScope, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            registryService: portal.RegistryService, $state: ng.ui.IStateService, 
            $modalInstance: any, id: string) {   
            this.scope = $scope
            $scope.regvm = this
            this.timeout = $timeout  
            this.configService = configService
            this.registryService = registryService
            this.state = $state
            this.modalInstance = $modalInstance
            this.database = this.configService.getDatabase(id)
            
            // watches changes of variable 
            // (is changed each time modal is opened)
            this.scope.$watch('this.database', 
                () => {        
                    this.getRepository(this.database.id) 
                    if(this.isFirstOpen) this.getSimulationModel(this.database.id)
                })
            
        }
        
        public getRepository(id: string) {
            this.initialising = true
            
            var cacheId = "repo-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.Repository().get(
                    { fmt: 'json' , id: id }).$promise
                
                this.registryPromise.then((spase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.repository)            
                    this.scope.$broadcast('update-repositories', cacheId)
                    this.initialising = false
                })
            } else {
                this.scope.$broadcast('update-repositories', cacheId)
                this.initialising = false
            }
        }
        
        // takes repository id
        public getSimulationModel(id: string) {
            this.scope.$broadcast('clear-simulation-models')
            this.loading = true
            var cacheId = "model-"+id
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.SimulationModel().get(
                    { fmt: 'json', id: id }).$promise
                
                this.registryPromise.then((spase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.simulationModel)
                    this.scope.$broadcast('update-simulation-models', cacheId)
                    this.loading = false
                })
            } else {
                this.scope.$broadcast('update-simulation-models', cacheId)
                this.loading = false
            }
        }
        
        // takes simulation model
        public getSimulationRun(element: SpaseElem) {
            this.scope.$broadcast('clear-simulation-runs', element)
            this.loading = true
            var cacheId = "run-"+element.resourceId
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.SimulationRun().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                
                this.registryPromise.then(
                (spase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.simulationRun)
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
        
        // takes simulation run
        public getNumericalOutput(element: SpaseElem) {
            this.scope.$broadcast('clear-numerical-outputs', element)
            this.loading = true
            var cacheId = "output-"+element.resourceId
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.NumericalOutput().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                
                this.registryPromise.then(
                (spase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.numericalOutput)
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
        
        // takes numerical output
        public getGranule(element: SpaseElem) {
            this.scope.$broadcast('clear-granules', element)
            this.loading = true
            var cacheId = "granule-"+element.resourceId
            
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.Granule().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                
                this.registryPromise.then(
                (spase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.granule)
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
        
        // methods for modal
        public saveRegistry() {
            this.modalInstance.close()
        }
        
        public cancelRegistry() {
            this.modalInstance.dismiss()
        }
        
    
    }
}