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
        public registryTooltip: string = 'Select suitable elements of the tree of the respective repository.<br/>'+
            'They will then be stored at &ldquo;My Data&rdquo; for further usage in the Data Access dialogs.<br/>'+
            'Via mouse over the element further information can be obtained.'
        
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
            this.scope.$watch('this.database', () => this.initRegistry())
            
            this.scope.$on('registry-filtered', () => this.initRegistry())
            
        }
        
        private initRegistry() {
            this.getRepository(this.database.id) 
            if(this.isFirstOpen) { 
                if(this.database.type == 'simulation')
                    this.getSimulationModel(this.database.id)
                else if(this.database.type == 'observation')
                    this.getObservatory(this.database.id)
             }
        }
        
        public getRepository(id: string) {
            this.initialising = true
            var cacheId = 'repo-'+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.Repository().get(
                    { fmt: 'json' , id: id }).$promise
                this.registryPromise.then((spase: ISpase) => {
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
            this.scope.$broadcast('clear-registry-dir')
            this.loading = true
            var cacheId = 'model-'+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.SimulationModel().get(
                    { fmt: 'json', id: id }).$promise
                this.registryPromise.then((spase: ISpase) => {
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
            var cacheId = 'run-'+element.resourceId
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.SimulationRun().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                this.registryPromise.then(
                (spase: ISpase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.simulationRun)
                    this.loading = false
                    this.scope.$broadcast('update-simulation-runs', cacheId)
                }, 
                (err: any) => { 
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
            var cacheId = 'output-'+element.resourceId
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.NumericalOutput().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                this.registryPromise.then(
                (spase: ISpase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.numericalOutput)
                    this.loading = false
                    this.scope.$broadcast('update-numerical-outputs', cacheId)
                }, 
                (err: any) => { 
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
            var cacheId = 'granule-'+element.resourceId
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.Granule().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                this.registryPromise.then(
                (spase: ISpase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.granule)
                    this.loading = false
                    this.scope.$broadcast('update-granules', cacheId)
                }, 
                (err: any) => { 
                    this.scope.$broadcast('registry-error', 'no granule found') 
                    this.loading = false 
                })
            } else {
                this.scope.$broadcast('update-granules', cacheId)
                this.loading = false   
            }  
        }
        
        // takes repository id
        public getObservatory(id: string) {
            this.scope.$broadcast('clear-registry-dir')
            this.loading = true
            var cacheId = 'obs-'+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.Observatory().get(
                    { fmt: 'json', id: id }).$promise
                this.registryPromise.then((spase: ISpase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.observatory)
                    this.scope.$broadcast('update-observatories', cacheId)
                    this.loading = false
                })
            } else {
                this.scope.$broadcast('update-observatories', cacheId)
                this.loading = false
            }
        }
        
        // takes observatory 
        public getInstrument(element: SpaseElem) {
            this.scope.$broadcast('clear-instruments', element)
            this.loading = true
            var cacheId = 'instr-'+element.resourceId
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.Instrument().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                this.registryPromise.then(
                (spase: ISpase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.instrument)
                    this.loading = false
                    this.scope.$broadcast('update-instruments', cacheId)
                }, 
                (err: any) => { 
                    this.scope.$broadcast('registry-error', 'no instrument found')
                    this.loading = false 
                })
            } else {
                this.scope.$broadcast('update-instruments', cacheId)
                this.loading = false
            }
        }
        
        // takes instrument
        public getNumericalData(element: SpaseElem) {
            this.scope.$broadcast('clear-numerical-data', element)
            this.loading = true
            var cacheId = 'data-'+element.resourceId
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.NumericalData().get(
                    { fmt: 'json', id: element.resourceId }).$promise
                this.registryPromise.then(
                (spase: ISpase) => {
                    this.registryService.cachedElements[cacheId] = spase.resources.map((r) => r.numericalData)
                    this.loading = false
                    this.scope.$broadcast('update-numerical-data', cacheId)
                }, 
                (err: any) => { 
                    this.scope.$broadcast('registry-error', 'no numerical data found')
                    this.loading = false 
                })
            } else {
                this.scope.$broadcast('update-numerical-data', cacheId)
                this.loading = false
            }
        }
        
        public printModalTitle(): string {
            return this.database.name+' '+
                this.database.type.charAt(0).toUpperCase()+
                this.database.type.slice(1)+'s'
        }
        
        // method for modal
        public saveRegistry(save: boolean) {
            if(save)
                this.modalInstance.close()
            else
                this.modalInstance.dismiss()
        }

    
    }
}