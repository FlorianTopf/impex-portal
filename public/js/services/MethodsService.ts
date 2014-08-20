/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes the actions for API methods
    export interface IMethodsAPI extends ng.resource.IResourceClass<ISwagger> {
        getMethods(): ISwagger
    }
    
    // describes the actions for service methods
    export interface IMethods extends ng.resource.IResourceClass<IResponse> {
        getMethod(): IResponse
        postMethod(): IResponse
    }
    
    export interface ILoaderMap {
        [resourceId: string]: boolean
    }
    
    export class MethodsService {
        static $inject: Array<string> = ['$rootScope', '$resource', 'growl']
        
        private scope: ng.IRootScopeService
        private resource: ng.resource.IResourceService
        private url: string = '/'
        private growl: any
        public methods: ISwagger = null    
        public loading: ILoaderMap = {}
        public status: string = ''
        public showError: ILoaderMap = {} 
        public showSuccess: ILoaderMap = {}
        
        // action descriptor for GET methods actions
        private methodsAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: false
        }
        
        // action descriptor for POST methods actions
        private postMethodAction: ng.resource.IActionDescriptor = {
            method: 'POST'
        }
        
        // generic method for requesting API
        private MethodsAPI(): IMethodsAPI {
            return <IMethodsAPI> this.resource(this.url+'api-docs/methods', 
                {}, { getMethods: this.methodsAction })
        }
          
        constructor($rootScope: ng.IRootScopeService, $resource: ng.resource.IResourceService, growl: any) {
            this.resource = $resource
            this.growl = growl
            this.scope = $rootScope
        }
        
        public notify(status: string, id: string){
            if(status == 'loading') {
                this.loading[id] = true
                this.showError[id] = false
                this.showSuccess[id] = false
                this.scope.$broadcast('service-loading', id)
                this.growl.info(this.status)
            } else if(status == 'success') {
                this.loading[id] = false
                this.showSuccess[id] = true
                this.scope.$broadcast('service-success', id)
                this.growl.success(this.status)
            } else if(status == 'error') {
                this.loading[id] = false
                this.showError[id] = true
                this.scope.$broadcast('service-error', id)
                this.growl.error(this.status)
            }
        }
        
        public getMethodsAPI(): ng.IPromise<ISwagger> {
            return this.MethodsAPI().get().$promise
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
        public requestMethod(path: string, params?: Object): IMethods {
            return <IMethods> this.resource(path, params,
                { getMethod: this.methodsAction, postMethod: this.postMethodAction})
        }   
        
        
    }
}
