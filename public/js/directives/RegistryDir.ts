/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IRegistryDirScope extends ng.IScope {
       regdirvm: RegistryDir
    }
    
    // active buttons map
    export interface IActiveMap {
        [resourceType: string]: SpaseElem 
    }

    export class RegistryDir implements ng.IDirective {

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
        
        private config: IConfig
        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private registryService: portal.RegistryService
        private myScope: ng.IScope 
        private oneAtATime: boolean = true
        private error: boolean = false
        private errorMessage: string = "no resources found"
        
        // container for intermediate results
        private repositories: Array<Repository> = []
        private simulationModels: Array<SimulationModel> = []
        private simulationRuns: Array<SimulationRun> = []
        private numericalOutputs: Array<NumericalOutput> = []
        private granules: Array<Granule> = []
        private activeItems: IActiveMap = {}

        constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            registryService: portal.RegistryService) {
            this.configService = configService
            this.registryService = registryService
            this.timeout = $timeout
            this.templateUrl = '/public/partials/templates/registryTree.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
            
        }

        linkFn($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.regdirvm = this
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
                this.activeItems = {}
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
        
        private setActive(type: string, element: SpaseElem) {
            this.activeItems[type] = element
        }
        
        private isActive(type: string, element: SpaseElem): boolean {
            return this.activeItems[type] === element
        }
        
        private trim(name: string, length: number = 25): string {
            if(name.length>length)
                 return name.slice(0, length).trim()+"..."
            else
                 return name.trim()
        }
        
        private format(name: string): string {
            return name.split("_").join(" ").trim()
        }
        
 
        
    }

}