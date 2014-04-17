/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // actions for registry
    export interface IRegistryResource extends ng.resource.IResourceClass<ISpase> {
        getRepository(): ISpase
        getSimulationModel(): ISpase
    }
    
    export class RegistryService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        private url: string = '/'
        
        // we will store here a map of loaded resources
        // we need do design an appropriate structure for that
        
        // action descriptor for registry actions
        private registryAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            params: {
                fmt: '@fmt'
            },
            isArray: false
        }  
          
        constructor($resource: ng.resource.IResourceService) {
            this.resource = $resource
        }
        
        public getRepository(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/repository?', 
                {fmt: '@fmt' },
                { getRepository: this.registryAction })
        }

    }
}
