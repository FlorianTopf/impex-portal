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
        public activeItems: IElementMap = {}
        // container for intermediate results
        public repositories: Array<Repository> = []
        public simulationModels: Array<SimulationModel> = []
        public simulationRuns: Array<SimulationRun> = []
        public numericalOutputs: Array<NumericalOutput> = []
        public granules: Array<Granule> = []
        public observatories: Array<Observatory> = []
        public instruments: Array<Instrument> = []
        public numericalData: Array<NumericalData> = []

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
            
            this.myScope.$watch('$includeContentLoaded', (e) => {  
                if(this.registryService.isFilterSet)
                    this.userService.user.activeSelection = []
                this.activeItems = {}
                this.showError = false
                this.status = ''
            })

            this.myScope.$on('registry-error', (e, msg: string) => {
                this.showError = true
                this.status = msg
            })
            
            this.myScope.$on('clear-registry-dir', (e) => {
                this.activeItems = {}
                this.showError = false
                this.simulationModels = []
                this.simulationRuns = []
                this.numericalOutputs = []
                this.granules = []
                this.observatories = []
                this.instruments = []
                this.numericalData = []
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
            
            this.myScope.$on('clear-instruments', (e, element: SpaseElem) => {
                this.setActive('Observatory', element)
                this.setInactive('Instrument')
                this.setInactive('NumericalData')
                this.showError = false
                this.instruments = []
                this.numericalData = []
            })
            
            this.myScope.$on('clear-numerical-data', (e, element: SpaseElem) => {
                this.setActive('Instrument', element)
                this.setInactive('NumericalData')
                this.showError = false
                this.numericalData = []
            })
   
            this.myScope.$on('update-repositories', (e, id: string) => {
                this.repositories = this.registryService.cachedElements[id].map((r) => <Repository>r)
            })
            
            this.myScope.$on('update-simulation-models', (e, id: string) => {
                this.simulationModels = this.registryService.cachedElements[id].map((r) => <SimulationModel>r)
                // hack forfiltering models of sinp
                this.simulationModels = this.simulationModels.filter((elem) => {
                    var region = elem.resourceHeader.resourceName.split(' ').reverse()[0]
                    if(this.registryService.selectedFilter[region])
                        return true
                    else if(!this.registryService.isFilterSet)
                        return true
                    else 
                        return false
                })
            })
            
            this.myScope.$on('update-simulation-runs', (e, id: string) => {
                this.simulationRuns = this.registryService.cachedElements[id].map((r) => <SimulationRun>r)
                // filtering the runs (when filter is active)
                this.simulationRuns = this.simulationRuns.filter((elem) => {
                    var matches = elem.simulatedRegion.filter((r) => { 
                        if(this.registryService.selectedFilter[r])
                            return true
                        else if(!this.registryService.isFilterSet)
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
            
            this.myScope.$on('update-observatories', (e, id: string) => {
                this.observatories = this.registryService.cachedElements[id].map((r) => <Observatory>r)
                // filtering the observatories (when filter is active)
                this.observatories = this.observatories.filter((elem) => {
                    var matches = elem.location.observatoryRegion.filter((r) => {
                        if(this.registryService.selectedFilter[r.split('.')[0]])
                            return true
                        else if(!this.registryService.isFilterSet)
                            return true
                        else 
                            return false
                    })
                    if(matches.length > 0)
                        return true
                })
            })
            
            this.myScope.$on('update-instruments', (e, id: string) => {
                this.instruments = this.registryService.cachedElements[id].map((r) => <Instrument>r)
            })
            
            this.myScope.$on('update-numerical-data', (e, id: string) => {
                this.numericalData = this.registryService.cachedElements[id].map((r) => <NumericalData>r)
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
        
        // needed within the html template
        public isActive(type: string, element: SpaseElem): boolean {
            return this.activeItems[type] === element
        }

        public trim(name: string, length: number = 25): string {
            if(name.length>length)
                 return name.slice(0, length).trim()+'...'
            else
                 return name.trim()
        }
        
        public format(name: string): string {
            return name.split('_').join(' ').trim()
        }
        
    }

}