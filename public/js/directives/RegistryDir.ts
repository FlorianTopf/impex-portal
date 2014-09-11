/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IRegistryDirScope extends ng.IScope {
       regdirvm: RegistryDir
    }
    
    export class RegistryDir implements ng.IDirective {

        public injection(): any[] {
            return [
                'registryService',
                'userService',
                (registryService: portal.RegistryService, userService: portal.UserService) => { 
                    return new RegistryDir(registryService, userService) }
            ]
        }
        
        public link: ($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        
        public oneAtATime: boolean = true
        public showError: boolean = false
        public status: string = ''
        
        public repositoryId: string = null
        // container for intermediate results
        public repositories: Array<Repository> = []
        public simulationModels: Array<SimulationModel> = []
        public simulationRuns: Array<SimulationRun> = []
        public numericalOutputs: Array<NumericalOutput> = []
        public granules: Array<Granule> = []
        public activeItems: IElementMap = {}
        
        private myScope: IRegistryDirScope
        private registryService: portal.RegistryService
        private userService: portal.UserService

        constructor(registryService: portal.RegistryService, userService: portal.UserService) {
            this.registryService = registryService
            this.userService = userService
            this.templateUrl = '/public/partials/templates/registryDir.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
        }

        linkFn($scope: portal.IRegistryDirScope, element: JQuery, attributes: ng.IAttributes): any {
            this.myScope = $scope
            $scope.regdirvm = this
            
            attributes.$observe('db', (id?: string)  => { 
                this.repositoryId = id
            })

            this.myScope.$on('registry-error', (e, msg: string) => {
                this.showError = true
                this.status = msg
            })
            
            this.myScope.$watch('$includeContentLoaded', (e) => {
                //console.log("RegistryDir loaded")   
                this.activeItems = {}
                this.showError = false
                this.status = ''
            })
            
            this.myScope.$on('clear-simulation-models', (e) => {
                this.activeItems = {}
                this.showError = false
                this.simulationModels = []
                this.simulationRuns = []
                this.numericalOutputs = []
                this.granules = []
            })
            
            this.myScope.$on('clear-simulation-runs', (e, element: SpaseElem) => {
                this.setActive('SimulationModel', element)
                this.setInactive('SimulationRun')
                this.setInactive('NumericalOutput')
                this.setInactive('Granule')
                this.showError = false
                this.simulationRuns = []
                this.numericalOutputs = []
                this.granules = []
            })
            
            this.myScope.$on('clear-numerical-outputs', (e, element: SpaseElem) => {
                this.setActive('SimulationRun', element)
                this.setInactive('NumericalOutput')
                this.setInactive('Granule')
                this.showError = false
                this.numericalOutputs = []
                this.granules = []
            })
            
            this.myScope.$on('clear-granules', (e, element: SpaseElem) => {
                this.setActive('NumericalOutput', element)
                this.setInactive('Granule')
                this.showError = false
                this.granules = []
            })
   
            this.myScope.$on('update-repositories', (e, id: string) => {
                this.repositories = this.registryService.cachedElements[id].map((r) => <Repository>r)
            })
            
            this.myScope.$on('update-simulation-models', (e, id: string) => {
                this.simulationModels = this.registryService.cachedElements[id].map((r) => <SimulationModel>r)
            })
            
            this.myScope.$on('update-simulation-runs', (e, id: string) => {
                this.simulationRuns = this.registryService.cachedElements[id].map((r) => <SimulationRun>r)
                // filtering the runs (when filter is active)
                this.simulationRuns = this.simulationRuns.filter((e) => {
                    var matches = e.simulatedRegion.filter((r) => { 
                        if(this.registryService.selectedFilter[r] == true)
                            return true
                        else if(this.registryService.isFilterSet == false)
                            return true
                        else
                            return false
                    })
                    if(matches.length > 0)
                        return true
                })
            })
            
            this.myScope.$on('update-numerical-outputs', (e, id: string) => {
                this.numericalOutputs = this.registryService.cachedElements[id].map((r) => <NumericalOutput>r)
            })
            
            this.myScope.$on('update-granules', (e, id: string) => {
                this.granules = this.registryService.cachedElements[id].map((r) => <Granule>r)
            })
            
        }
        
        public setActive(type: string, element: SpaseElem) {
            this.activeItems[type] = element
            // new selection id
            var id = this.userService.createId()
            this.userService.user.activeSelection = [new Selection(this.repositoryId, id, type, element)]
            this.myScope.$broadcast('update-selections', id)
        }
        
        public setInactive(type: string) {
            this.activeItems[type] = null
        }
        
        public isActive(type: string, element: SpaseElem): boolean {
            return this.activeItems[type] === element
        }
       
        public trim(name: string, length: number = 25): string {
            if(name.length>length)
                 return name.slice(0, length).trim()+"..."
            else
                 return name.trim()
        }
        
        public format(name: string): string {
            return name.split("_").join(" ").trim()
        }
        
    }

}