/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions for config
    export interface IConfigResource extends ng.resource.IResourceClass<IConfig> {
        getConfig(): IConfig
    }

    export class ConfigService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        private url: string = '/'
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
            return <IConfigResource> this.resource(this.url+'config?', 
                { fmt: '@fmt' }, 
                { getConfig: this.configAction })
        } 
        
        // returns promise for resource handler
        public loadConfig(): ng.IPromise<IConfig> {
            return this.getConfig().get({fmt: 'json' }).$promise
        }
        
        public getDatabase(id: string): Database {
            return this.config.databases.filter((e) => e.id == id)[0]
        }
        

        

    }
}
