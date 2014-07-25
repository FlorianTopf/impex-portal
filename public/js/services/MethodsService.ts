/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions for API methods
    export interface IMethodsAPIResource extends ng.resource.IResourceClass<ISwagger> {
        getMethods(): ISwagger
    }
    
    // describes the actions for service methods
    export interface IMethodsResource extends ng.resource.IResourceClass<IResponse> {
        requestMethod(): IResponse
    }
    
    export class MethodsService {
        static $inject: Array<string> = ['$resource']
        
        private resource: ng.resource.IResourceService
        private url: string = ''
        public methods: ISwagger = null    
        public loading: boolean = false
        public status: string = ''
        public showError: boolean = false  
        
        // action descriptor for registry actions
        private methodsAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }
          
        constructor($resource: ng.resource.IResourceService) {
            this.resource = $resource
        }
        
        // generic method for requesting 
        public getMethodsAPI(): IMethodsAPIResource {
            return <IMethodsAPIResource> this.resource(this.url+this.url+'api-docs/methods', 
                { getMethods: this.methodsAction })
        }
        
        public getMethods(db: Database): Array<Api> {
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
        
        // generic method for requesting standard services (GET + params)
        public requestMethod(path: string, params: Object): IMethodsResource {
            return <IMethodsResource> this.resource(path,
                params,
                { requestMethod: this.methodsAction })
        }
        


    }
}
