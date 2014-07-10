/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IRegistryScope extends ng.IScope {
        regvm: RegistryCtrl
    }

    // @TODO introduce error/offline handling later
    export class RegistryCtrl {
        private scope: portal.IRegistryScope
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private window: ng.IWindowService
        private configService: portal.ConfigService
        private registryService: portal.RegistryService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        private modalInstance: any
        private registryPromise: ng.IPromise<ISpase>
        
        public database: Database = null
        public initialising: boolean = false
        public loading: boolean = false
        public transFinished: boolean = true
        
        static $inject: Array<string> = ['$scope', '$location', '$timeout', '$window',
            'configService', 'registryService', 'userService', '$state', '$modalInstance', 'id']

        constructor($scope: IRegistryScope, $location: ng.ILocationService, $timeout: ng.ITimeoutService, 
            $window: ng.IWindowService, configService: portal.ConfigService, 
            registryService: portal.RegistryService, userService: portal.UserService, 
            $state: ng.ui.IStateService, $modalInstance: any, id: string) {   
            this.scope = $scope
            $scope.regvm = this
            this.location = $location
            this.timeout = $timeout  
            this.window = $window 
            this.configService = configService
            this.registryService = registryService
            this.userService = userService
            this.state = $state
            this.modalInstance = $modalInstance
            
            this.database = this.configService.getDatabase(id)
            console.log("Registry Ctrl")
            
              // watches changes of variable 
            //(is changed each time modal is opened)
            this.scope.$watch('this.database', 
                () => { this.getRepository(this.database.id) })
            
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
        
        public getSimulationModel(id: string) {
            this.scope.$broadcast('clear-simulation-models')
            this.loading = true
            
            var cacheId = "model-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.getSimulationModel().get(
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
        
        public getSimulationRun(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-simulation-runs', element)
            this.loading = true
            
            var cacheId = "run-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.getSimulationRun().get(
                    { fmt: 'json', id: id }).$promise
                
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
        
        public getNumericalOutput(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-numerical-outputs', element)
            this.loading = true
            
            var cacheId = "output-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.getNumericalOutput().get(
                    { fmt: 'json', id: id }).$promise
                
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
        
        public getGranule(id: string, element: SpaseElem, $event: Event) {
            this.scope.$broadcast('clear-granules', element)
            this.loading = true
            
            var cacheId = "granule-"+id
            if(!(cacheId in this.registryService.cachedElements)) {  
                this.registryPromise = this.registryService.getGranule().get(
                    { fmt: 'json', id: id }).$promise
                
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
        
        // testing methods for modal
        public saveRegistry() {
            this.modalInstance.close()
            // @TODO just for the moment
            this.scope.$broadcast('clear-registry')
        }
        
        public cancelRegistry() {
            this.modalInstance.dismiss()
            // @TODO just for the moment
            this.scope.$broadcast('clear-registry')
        }
        
    
    }
}