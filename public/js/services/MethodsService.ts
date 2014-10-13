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
    
    export class MethodsService {
        static $inject: Array<string> = ['$rootScope', '$resource', 'growl']
        
        private scope: ng.IRootScopeService
        private resource: ng.resource.IResourceService
        private url: string = '/'
        private growl: any
        
        public methods: ISwagger = null    
        // special applyables for SINP models/outputs
        public applyableModels: IArrayMap = {}
        public status: string = ''
        public responseLog: Array<ResponseLog> = [] 
        public loading: IBooleanMap = {}
        public showError: IBooleanMap = {} 
        public showSuccess: IBooleanMap = {}
        public unreadResults: number = 0
        
        constructor($rootScope: ng.IRootScopeService, $resource: ng.resource.IResourceService, growl: any) {
            this.resource = $resource
            this.growl = growl
            this.scope = $rootScope
            
            // @TODO fill manual applyables for SINP (no API info available)
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Earth/OnFly'] = 
            ['calculateDataPointValue', 'calculateDataPointValueSpacecraft', 'calculateDataPointValueFixedTime', 
                'calculateFieldline', 'calculateCube', 'calculateFieldline', 'getSurfaceSINP']
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Mercury/OnFly'] = 
                ['calculateDataPointValueNercury', 'calculateCubeMercury']
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Saturn/OnFly'] = 
                ['calculateDataPointValueSaturn', 'calculateCubeSaturn']
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Jupiter/OnFly'] = 
                ['calculateDataPointValueJupiter', 'calculateCubeJupiter']
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/GiantPlanet/OnFly'] = 
                ['calculateDataPointValueGiantPlanets']
        }
        
        // action descriptor for GET methods actions
        private getMethodAction: ng.resource.IActionDescriptor = {
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
                {}, { getMethods: this.getMethodAction })
        }
        
        public getMethodsAPI(): ng.IPromise<ISwagger> {
            return this.MethodsAPI().get().$promise
        }
        
        public getMethods(db: Database): Array<Api> {
            // we only fetch the needed apis
            // for FMI we need a special selector
            if(db.name.indexOf('FMI') != -1) {
                return this.methods.apis.filter((e) => { 
                    if(e.path.indexOf('FMI') != -1)
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
                { getMethod: this.getMethodAction, postMethod: this.postMethodAction})
        }   
        
        public notify(status: string, id: string) {
            if(status == 'loading') {
                this.status = 'Loading data from service'
                this.loading[id] = true
                this.showError[id] = false
                this.showSuccess[id] = false
                this.growl.info(this.status)
            } else if(status == 'error') {
                this.loading[id] = false
                this.showError[id] = true
                this.growl.error(this.status)
                this.responseLog = 
                    [new ResponseLog(new Date(), this.status, id)].concat(this.responseLog)
            } else if(status == 'success') {
                this.status = 'Added service result to user data'
                this.unreadResults++
                this.loading[id] = false
                this.showSuccess[id] = true
                this.scope.$broadcast('service-success', id)
                this.growl.success(this.status)
                this.responseLog = 
                    [new ResponseLog(new Date(), this.status, id)].concat(this.responseLog)
            } 
            //console.log(JSON.stringify(this.responseLog))
        }
        
    }
}
