/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions for registry
    export interface IRegistryResource extends ng.resource.IResourceClass<ISpase> {
        getRepository(): ISpase
        getSimulationModel(): ISpase
        getSimulationRun(): ISpase
        getNumericalOutput(): ISpase
        getGranule(): ISpase
    }
    
    export class RegistryService {
        static $inject: Array<string> = ['$rootScope', '$resource']
        
        private resource: ng.resource.IResourceService
        private scope: ng.IRootScopeService
        private url: string = '/'
        public isFilterSet: boolean = false
        public selectedFilter: IBooleanMap = {}
        // cache for the elements (identified by request id)
        public cachedElements: IElementArrayMap = {}
        // defines which elements are selectables in the registry
        public selectables: IArrayMap = {}
        
        // action descriptor for registry actions
        private registryAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }
          
        constructor($rootScope: ng.IRootScopeService, $resource: ng.resource.IResourceService) {
            this.resource = $resource
            this.scope = $rootScope
            this.selectables['spase://IMPEX/Repository/FMI/HYB'] = [ 'NumericalOutput'  ]
            this.selectables['spase://IMPEX/Repository/FMI/GUMICS'] = [ 'NumericalOutput' ]
            this.selectables['spase://IMPEX/Repository/LATMOS'] = [ 'SimulationRun', 'NumericalOutput' ]
            this.selectables['spase://IMPEX/Repository/SINP'] = [ 'SimulationModel', 'NumericalOutput' ]
        }
        
        public notify(status: string, id: string) {
             if(status == 'success') {
                this.scope.$broadcast('database-success', id)
              }
        }
        
        public Repository(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/repository?', 
                { id: '@id', fmt: '@fmt' },
                { getRepository: this.registryAction })
        }
        
        public SimulationModel(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/simulationmodel?',
                { id: '@id', fmt: '@fmt' },
                { getSimulationModel: this.registryAction })
        }

        public SimulationRun(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/simulationrun?',
                { id: '@id', fmt: '@fmt' },
                { getSimulationRun: this.registryAction })
        }
        
        public NumericalOutput(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/numericaloutput?',
                { id: '@id', fmt: '@fmt' },
                { getNumericalOutput: this.registryAction })
        }
        
        public Granule(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/granule?',
                { id: '@id', fmt: '@fmt' },
                { getGranule: this.registryAction })
        }

        
    }
}
