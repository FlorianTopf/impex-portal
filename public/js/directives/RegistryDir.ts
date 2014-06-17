/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IRegistryDirScope extends ng.IScope {
       regvm: RegistryDir
    }
    
    // active buttons map
    export interface IActiveMap {
        [resourceType: string]: SpaseElem 
    }

    export class RegistryDir {

        // @TODO here we add dependencies for the used elements in bootstrap-ui
        public injection(): any[] {
            return [
                '$timeout',
                'configService',
                'registryService',
                ($timeout: ng.ITimeoutService, configService: portal.ConfigService, 
                    registryService: portal.RegistryService) => { 
                    return new RegistryDir($timeout, configService, registryService); }
            ]
        }
        
        public link: ($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        
        private configService: portal.ConfigService
        private registryService: portal.RegistryService
        private timeout: ng.ITimeoutService
        private myScope: ng.IScope 
        private oneAtATime: boolean = true
        private error: boolean = false
        private errorMessage: string = "no resources found"
        
        // container for intermediate results
        public repositories: Array<Repository> = []
        public simulationModels: Array<SimulationModel> = []
        public simulationRuns: Array<SimulationRun> = []
        public numericalOutputs: Array<NumericalOutput> = []
        public granules: Array<Granule> = []
        public activeItems: IActiveMap = {}

        constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            registryService: portal.RegistryService) {
            this.configService = configService
            this.registryService = registryService
            this.timeout = $timeout
            this.templateUrl = '/public/partials/templates/registry.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
            
        }
        
        public setActive(type: string, element: SpaseElem) {
            this.activeItems[type] = element
        }
        
        public isActive(type: string, element: SpaseElem) {
            return this.activeItems[type] === element
        }

        linkFn($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.regvm = this
            this.myScope = $scope
            
            $scope.$on('registry-error', (e, msg: string) => {
                this.error = true
                this.errorMessage = msg
            })
            
            $scope.$on('clear-registry', (e) => {
                console.log("clearing registry")
                this.activeItems = {}
                this.error = false
                this.repositories = []
                this.simulationModels = []
                this.simulationRuns = []
                this.numericalOutputs = []
                this.granules = []
            })
            
            $scope.$on('clear-simulation-models', (e) => {
                this.error = false
                this.simulationModels = []
                this.simulationRuns = []
                this.numericalOutputs = []
                this.granules = []
            })
            
            $scope.$on('clear-simulation-runs', (e, element: SpaseElem) => {
                this.setActive("model", element)
                this.error = false
                this.simulationRuns = []
                this.numericalOutputs = []
                this.granules = []
            })
            
            $scope.$on('clear-numerical-outputs', (e, element: SpaseElem) => {
                this.setActive("run", element)
                this.error = false
                this.numericalOutputs = []
                this.granules = []
            })
            
            $scope.$on('clear-granules', (e, element: SpaseElem) => {
                this.setActive("output", element)
                this.error = false
                this.granules = []
            })
   
            $scope.$on('update-repositories', (e, id: string) => {
                this.repositories = this.registryService.cachedElements[id].map((r) => <Repository>r)
            })
            
            $scope.$on('update-simulation-models', (e, id: string) => {
                this.simulationModels = this.registryService.cachedElements[id].map((r) => <SimulationModel>r)

            })
            
            $scope.$on('update-simulation-runs', (e, id: string) => {
                this.simulationRuns = this.registryService.cachedElements[id].map((r) => <SimulationRun>r)
            })
            
            $scope.$on('update-numerical-outputs', (e, id: string) => {
                this.numericalOutputs = this.registryService.cachedElements[id].map((r) => <NumericalOutput>r)
            })
            
            $scope.$on('update-granules', (e, id: string) => {
                this.granules = this.registryService.cachedElements[id].map((r) => <Granule>r)
            })
            
        }
        
 
        
    }

}