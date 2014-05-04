/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // actions for registry
    export interface IRegistryResource extends ng.resource.IResourceClass<ISpase> {
        getRepository(): ISpase
        getSimulationModel(): ISpase
        getSimulationRun(): ISpase
        getNumericalOutput(): ISpase
        getGranule(): ISpase
    }
    
    // simple resource map
    export interface elements {
        [index: string]: ISpase
    }
    
    export class RegistryService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        private url: string = '/'
        
        // action descriptor for registry actions
        private registryAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            params: {
                id: '@id',
                fmt: '@fmt'
            },
            isArray: false
        }
          
        constructor($resource: ng.resource.IResourceService) {
            this.resource = $resource
        }
        
        // @TODO we will store here a map of loaded resources
        // we need do design an appropriate structure for that
        // then we can make a cache out of it
        public repositories: Array<Repository> = []
        public simulationModels: Array<SimulationModel> = []
        public simulationRuns: Array<SimulationRun> = []
        
        public getRepository(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/repository?', 
                { id: '@id', fmt: '@fmt' },
                { getRepository: this.registryAction })
        }
        
        // @TODO we need to be able to provide an r (recursive) param
        public getSimulationModel(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/simulationmodel?',
                { id: '@id', fmt: '@fmt' },
                { getSimulationModel: this.registryAction })
        }

        // @TODO we need to be able to provide an r (recursive) param
        public getSimulationRun(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/simulationrun?',
                { id: '@id', fmt: '@fmt' },
                { getSimulationRun: this.registryAction })
        }
        
        // @TODO we need to be able to provide an r (recursive) param
        public getNumericalOutput(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/numericaloutput?',
                { id: '@id', fmt: '@fmt' },
                { getNumericalOutput: this.registryAction })
        }
        
        // @TODO we need to be able to provide an r (recursive) param
        public getGranule(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/granule?',
                { id: '@id', fmt: '@fmt' },
                { getGranule: this.registryAction })
        }

    }
}
