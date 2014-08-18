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
        postMethod(): IResponse
    }
    
    export class MethodsService {
        static $inject: Array<string> = ['$resource', 'growl']
        
        private resource: ng.resource.IResourceService
        private url: string = '/'
        private growl: any
        public methods: ISwagger = null    
        
        // action descriptor for GET methods actions
        private methodsAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }
        
        // action descriptor for POST methods actions
        private postMethodAction: ng.resource.IActionDescriptor = {
            method: 'POST'
        }
          
        constructor($resource: ng.resource.IResourceService, growl: any) {
            this.resource = $resource
            this.growl = growl
        }
        
        public notify(status: string){
            if(status == 'success')
                this.growl.success('Added service result to user data')
             else
                 this.growl.error(status)
        }
        
        // generic method for requesting API
        public MethodsAPI(): IMethodsAPIResource {
            return <IMethodsAPIResource> this.resource(this.url+'api-docs/methods', 
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
        
        // generic method for requesting standard services (GET + params / POST)
        public requestMethod(path: string, params?: Object): IMethodsResource {
            return <IMethodsResource> this.resource(path, params,
                { requestMethod: this.methodsAction, postMethod: this.postMethodAction })
        }   


    }
}
