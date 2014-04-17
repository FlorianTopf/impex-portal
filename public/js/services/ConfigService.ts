/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions
    export interface IConfigResource extends ng.resource.IResourceClass<IConfig> {
        getConfig(): IConfig
    }

    export class ConfigService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        public url: string = '/'
        public config: IConfig = null      
        
        // creates an action descriptor
        private configAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }  
        
        constructor($resource: ng.resource.IResourceService) {
            this.resource = $resource
        }
        
        // returns the resource handler 
        public getConfig(): IConfigResource {
            return <IConfigResource> this.resource(this.url+'config?fmt=json', null, 
                { getConfig: this.configAction })
        }      

    }
}
