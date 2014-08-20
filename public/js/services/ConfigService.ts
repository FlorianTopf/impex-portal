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
        public filterRegions: Array<string> = []
        
        // creates an action descriptor
        private configAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }  
        
        constructor($resource: ng.resource.IResourceService, $http: ng.IHttpService) {
            this.resource = $resource
            this.http = $http
        }
        
        // checks if a database and its methods are alive
        public isAlive(dbName: string) {
           // little hack for FMI (it's the same method)
           if((dbName.indexOf('FMI-HYBRID') != -1) || (dbName.indexOf('FMI-GUMICS') != -1))
             var callName = 'FMI'
           else
             var callName = dbName
               
           this.http.get(this.url+'methods/'+callName+"/isAlive", { timeout: 10000})
               .success((data: boolean, status: any) => { 
                    this.aliveMap[dbName] = data
                    //console.log("Hello "+dbName+" "+this.aliveMap[dbName]) 
                })
               .error((data: any, status: any) => { this.aliveMap[dbName] = false })           
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
        
        // Filter routines
        public getRegions() {
            this.http.get(this.url+'filter/region', { timeout: 10000})
                .success((data: Array<string>, status: any) => this.filterRegions = data)
                .error((data: any, status: any) => this.filterRegions = [])
        }
        
        public filterRegion(name: String): ng.IHttpPromise<Array<string>> {
            return this.http.get(this.url+'filter/region/'+name, { timeout: 10000})
        }
        

    }
}
