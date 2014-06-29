/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // actions for registry
    /* export interface IRegistryResource extends ng.resource.IResourceClass<ISpase> {
        getRepository(): ISpase
        getSimulationModel(): ISpase
        getSimulationRun(): ISpase
        getNumericalOutput(): ISpase
        getGranule(): ISpase
    }*/
    
    // @TODO here we need to add access to all relevant API-docs and methods routes
    export class MethodsService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        private url: string = '/'
        
        // action descriptor for registry actions
        /* private registryAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }*/
          
        constructor($resource: ng.resource.IResourceService) {
            this.resource = $resource
        }
        

        
        /*public getRepository(): IRegistryResource {
            return <IRegistryResource> this.resource(this.url+'registry/repository?', 
                { id: '@id', fmt: '@fmt' },
                { getRepository: this.registryAction })
        }*/
        


    }
}
