/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IMethodsScope extends ng.IScope {
        methvm: MethodsCtrl;
    }
    
    // special applyables for SINP models/outputs
    export interface IApplyableModels {
        [id: string]: Array<string>
    }

    export class MethodsCtrl {
        private scope: portal.IMethodsScope
        private window: ng.IWindowService
        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private methodsService: portal.MethodsService
        private userService: portal.UserService 
        private state: ng.ui.IStateService
        private modalInstance: any
        private methodsPromise: ng.IPromise<any>
        // special applyables for SINP models/outputs
        private applyableModels: IApplyableModels
        private database: Database
        
        public methods: Array<Api> = []
        public initialising: boolean = false
        public status: string = ''
        public showError: boolean = false  
        public currentMethod: Api = null
        public request: Object = {}
        
        static $inject: Array<string> = ['$scope', '$timeout', '$window', 'configService', 'methodsService', 
            'userService', '$state', '$modalInstance', 'id']

        constructor($scope: IMethodsScope, $timeout: ng.ITimeoutService, $window: ng.IWindowService, configService: portal.ConfigService, 
            methodsService: portal.MethodsService, userService: portal.UserService,
            $state: ng.ui.IStateService, $modalInstance: any, id: string) {   
            this.scope = $scope
            $scope.methvm = this   
            this.timeout = $timeout
            this.window = $window
            this.configService = configService
            this.methodsService = methodsService
            this.userService = userService
            this.state = $state
            this.modalInstance = $modalInstance
            this.applyableModels = {}
            this.database = this.configService.getDatabase(id)
            
            if(this.methodsService.methods) {
                this.methods = this.methodsService.getMethods(this.database)
                // check sessionStorage if there is a saved state
                if(this.database.id in this.userService.sessionStorage.methods) {
                    // we must do it with timeout (since we send broadcasts and dir is not fully loaded)
                    this.timeout(() => this.setActive(this.methods.filter(
                        (m) => m.path == this.userService.sessionStorage.methods[this.database.id].path)[0]))
                 }
            } else {
                this.loadMethodsAPI()
            }
                
            // fill manual applyables for SINP
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Earth/OnFly'] = 
            ['getDataPointValueSINP', 'calculateDataPointValue', 'calculateDataPointValueSpacecraft', 'calculateDataPointValueFixedTime', 
                'calculateFieldline', 'calculateCube', 'calculateFieldline', 'getSurfaceSINP']
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Mercury/OnFly'] = ['calculateDataPointValueNercury', 'calculateCubeMercury']
            this.applyableModels['spase://IMPEX/SimulationModel/SINP/Saturn/OnFly'] = ['calculateDataPointValueSaturn', 'calculateCubeSaturn']

        }
        
        private loadMethodsAPI() {
            this.initialising = true
            this.status = ''
            this.methodsService.MethodsAPI().get(
                (data: ISwagger, status: any) => this.handleAPIData(data, status),
                (error: any) => this.handleAPIError(error)
            )
        }
        
        private handleAPIData(data: ISwagger, status?: any) {
            this.initialising = false
            this.status = 'success'
            // we always get the right thing
            this.methodsService.methods = data
            this.methods = this.methodsService.getMethods(this.database)
            // check sessionStorage if there is a saved state
            if(this.database.id in this.userService.sessionStorage.methods) {
               this.setActive(this.methods.filter(
                   (m) => m.path == this.userService.sessionStorage.methods[this.database.id].path)[0])
            } 
        } 
        
        private handleAPIError(error: any) {
            this.initialising = false
            if(this.window.confirm('connection timed out. retry?'))
                this.loadMethodsAPI()
            else {
                this.showError = true
                if(error.status = 404)
                    this.status = error.status+' resource not found'
                else
                    this.status = error.data+' '+error.status
            }
        }  
        
        // handling and saving the WS result
        private handleServiceData(data: IResponse, status?: any) {
            //console.log('success: '+JSON.stringify(data.message))
            this.methodsService.loading = false
            this.methodsService.status = 'success'
            // @TODO change id creation later
            var id = this.userService.createId()
            this.userService.user.results.push(new Result(this.database.id, id, this.currentMethod.path, data))
            // refresh localStorage
            this.userService.localStorage.results = this.userService.user.results
            this.scope.$broadcast('update-results', id)
        }
        
        private handleServiceError(error: any) {
            //console.log('failure: '+error.status)
            this.methodsService.loading = false
            this.methodsService.showError = true
            if(error.status == 404)
                this.methodsService.status = error.status+' resource not found'
            else {
                var response = <IResponse>error.data
                this.methodsService.status = response.message
            }
        }
        
        // method for submission
        public submitMethod() {
            //console.log('submitted '+this.currentMethod.path+' '+this.request['id'])
            this.methodsService.loading = true
            this.methodsService.status = ''
            this.methodsService.showError = false
            this.methodsService.requestMethod(this.currentMethod.path, this.request).get(
                (data: IResponse, status: any) => this.handleServiceData(data, status),
                (error: any) => this.handleServiceError(error)
            )
        }
        
        public retry() {
            this.showError = false
            this.loadMethodsAPI()
        }
        
        // helpers for methods modal
        public dropdownStatus = {
            isopen: false,
            active: 'Choose Method'
        }
        
        // set a method active and forward info to directives
        public setActive(method: Api) {
            this.dropdownStatus.active = this.trimPath(method.path)
            this.currentMethod = method
            
            // there is only one operation per method
            this.currentMethod.operations[0].parameters.forEach((p) => {
                this.request[p.name] = p.defaultValue     
            })
            
            // refresh session storage
            if(this.database.id in this.userService.sessionStorage.methods) {
                if(this.userService.sessionStorage.methods[this.database.id].path == this.currentMethod.path)
                    this.request = this.userService.sessionStorage.methods[this.database.id].params
                else
                    this.userService.sessionStorage.methods[this.database.id] = 
                        new MethodState(method.path, this.request) 
            } else {
                this.userService.sessionStorage.methods[this.database.id] = 
                    new MethodState(method.path, this.request)
            }
            
            // check if there is an id field and broadcast applyable elements
            if(this.currentMethod.operations[0].parameters.filter((e) => e.name == 'id').length != 0) {
                // there is only one id param per method 
                var param = this.currentMethod.operations[0].parameters.filter((e) => e.name == 'id')[0]
                this.scope.$broadcast('set-applyable-elements', param.description)
            } else {
                // if there is no id, broadcast empty string
                this.scope.$broadcast('set-applyable-elements', '')
            }
            
            // check if there is an url field and broadcast indication
            if(this.currentMethod.operations[0].parameters.filter((e) => e.name == 'votable_url').length != 0) {
                this.scope.$broadcast('set-applyable-votable', true)
            } else {
                // if there is no url field return false
                this.scope.$broadcast('set-applyable-votable', false)
            }
            
            // if there is a SINP method chosen, we must forward info about applyable models
            if(this.currentMethod.path.indexOf('SINP') != -1) {
               //console.log(this.currentMethod.operations[0].nickname)
               for(var key in this.applyableModels) {
                    var methods = this.applyableModels[key]
                    var index = methods.indexOf(this.currentMethod.operations[0].nickname)
                    if(index != -1) this.scope.$broadcast('set-applyable-models', key) 
               }
            }
            
        }
        
        public isActive(path: string): boolean {
            return this.dropdownStatus.active == this.trimPath(path)
        }
        
        public getActive(): string {
            return this.dropdownStatus.active
        }
        
        public trimPath(path: string): string {
            var splitPath = path.split('/').reverse()
            return splitPath[0]
        }
       
        // method for applying a selection to the current method
        public applySelection(resourceId: string) {
            //console.log("applySelection "+resourceId)
            this.request['id'] = resourceId
        }
        
        // method for applying a votable url to the current method
        public applyVOTable(url: string) {
            //console.log("applyVOTable "+url)
            this.request['votable_url'] = url
        }
        
        public updateRequest(paramName: string) {
            //console.log("Update "+this.request[paramName])
            this.userService.sessionStorage.methods[this.database.id]
                .params[paramName] = this.request[paramName]
        }

        // methods for modal
        public saveMethods() {
            this.modalInstance.close()
            this.methodsService.showError = false
        }
        
        public cancelMethods() {
            this.modalInstance.dismiss()
            this.methodsService.showError = false
        }
        
    }
}