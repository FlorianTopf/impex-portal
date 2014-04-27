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
    
    export class RegistryService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        private url: string = '/'
        
        // @TODO we will store here a map of loaded resources
        // we need do design an appropriate structure for that
        
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
