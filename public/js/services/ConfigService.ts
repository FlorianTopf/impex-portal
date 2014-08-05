/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions for config
    export interface IConfigResource extends ng.resource.IResourceClass<IConfig> {
        getConfig(): IConfig
    }
    
    // isAlive map
    export interface IAliveMap {
        [id: string]: Boolean
    }

    export class ConfigService {
        static $inject: Array<string> = ['$resource', '$http']
        
        private resource: ng.resource.IResourceService
        private http: ng.IHttpService
        private url: string = '/'
        public config: IConfig = null
        public aliveMap: IAliveMap = {}
        
        // creates an action descriptor
        private configAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }  
        
        // checks if a database and its methods are alive
        public isAlive(dbName: string) {
           // little hack for FMI (it's the same method)
           if((dbName.indexOf('FMI-HYBRID') != -1) || (dbName.indexOf('FMI-GUMICS') != -1))
             var callName = 'FMI'
           else
             var callName = dbName
               
           this.http.get(this.url+'methods/'+callName+"/isAlive", { timeout: 10000})
               .success((data: boolean, status: any) => { this.aliveMap[dbName] = true; console.log("Hello "+dbName); })
               .error((data: any, status: any) => { this.aliveMap[dbName] = false })           
        }
        
        constructor($resource: ng.resource.IResourceService, $http: ng.IHttpService) {
            this.resource = $resource
            this.http = $http
        }
        
        // returns the resource handler 
        public Config(): IConfigResource {
            return <IConfigResource> this.resource(this.url+'config?', 
                { fmt: '@fmt' }, 
                { getConfig: this.configAction })
        } 
        
        // returns promise for resource handler
        public loadConfig(): ng.IPromise<IConfig> {
            return this.Config().get({fmt: 'json' }).$promise
        }
        
        public getDatabase(id: string): Database {
            return this.config.databases.filter((e) => e.id == id)[0]
        }
        

    }
}
