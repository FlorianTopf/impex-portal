/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions for config
    export interface IConfigResource extends ng.resource.IResourceClass<IConfig> {
        getConfig(): IConfig
    }

    export class ConfigService {
        static $inject: Array<string> = ['$resource', '$http']
        
        private resource: ng.resource.IResourceService
        private http: ng.IHttpService
        private url: string = '/'
        public config: IConfig = null
        public aliveMap: IBooleanMap = {}
        public filterRegions: Array<string> = []
        public filterMap: IBooleanMap = {}
        
        // creates an action descriptor
        private configAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
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
        
        // isAlive routine
        public isAlive(db: Database) {
           // little hack for FMI (it's the same method)
           if((db.name.indexOf('FMI-HYBRID') != -1) || (db.name.indexOf('FMI-GUMICS') != -1))
             var callName = 'FMI'
           else
             var callName = db.name
               
           this.http.get(this.url+'methods/'+callName+'/isAlive', { timeout: 10000})
               .success((data: boolean, status: any) => { 
                   this.aliveMap[db.id] = data
                   //console.log('Hello '+db.name+' '+this.aliveMap[db.id]) 
               })
               .error((data: any, status: any) => { 
                   this.aliveMap[db.id] = false 
               })           
        }
        
        // Filter routines
        public loadRegions(): ng.IHttpPromise<Array<string>> {
            return this.http.get(this.url+'filter/region', { timeout: 10000})
        }
        
        public filterRegion(name: String): ng.IHttpPromise<Array<string>> {
            return this.http.get(this.url+'filter/region/'+name, { timeout: 10000})
        }
        

    }
}
