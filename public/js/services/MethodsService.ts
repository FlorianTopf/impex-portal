/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions for methods
    export interface IMethodsResource extends ng.resource.IResourceClass<ISpase> {
        getMethods(): ISwagger
    }
    
    export class MethodsService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        private url: string = '/'
        public methods: ISwagger = null    
        
        // action descriptor for registry actions
        private methodsAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }
          
        constructor($resource: ng.resource.IResourceService) {
            this.resource = $resource
        }
        
        public getMethodsAPI(): IMethodsResource {
            return <IMethodsResource> this.resource(this.url+'api-docs/methods', 
                // we can remove the params here!
                {},{ getMethods: this.methodsAction })
        }
        
        public getMethods(db: Database) {
            // we only fetch the needed apis
            // for FMI we need a special selector
            if(db.name.indexOf("FMI") != -1) {
                return this.methods.apis.filter((e) => { 
                    if(e.path.indexOf("FMI") != -1)
                        return true
                    else 
                        return false
                })
            } else {
                return this.methods.apis.filter((e) => {
                    if(e.path.indexOf(db.name) != -1)
                        return true
                    else
                        return false
                }) 
            }
        }
        


    }
}
