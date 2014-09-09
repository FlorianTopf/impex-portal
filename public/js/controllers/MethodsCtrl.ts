/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IMethodsScope extends ng.IScope {
        methvm: MethodsCtrl;
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
        private database: Database
        
        public methods: Array<Api> = []
        public initialising: boolean = false
        public status: string = ''
        public showError: boolean = false
        public methodsTooltip: string = "Select one of the stored elements, including uploaded VOTables,<br/>"+
            "to be applied to the available methods of the IMPEx services.<br/>"+
            "Please be aware that only those selections, which are applicable for the<br/>"+
            "respective methods can be applied."
        
        static $inject: Array<string> = ['$scope', '$timeout', '$window', 'configService', 'methodsService', 
            'userService', '$state', '$modalInstance', 'id']

        constructor($scope: IMethodsScope, $timeout: ng.ITimeoutService, $window: ng.IWindowService, 
            configService: portal.ConfigService, methodsService: portal.MethodsService, userService: portal.UserService,
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
            this.database = this.configService.getDatabase(id)

            if(this.methodsService.methods) {
                this.methods = this.methodsService.getMethods(this.database)
                // check sessionStorage if there is a saved state
                if(this.database.id in this.userService.sessionStorage.methods) {
                    this.setActive(this.methods.filter(
                        (m) => m.path == this.userService.sessionStorage.methods[this.database.id].path)[0])
                 }
            } else {
                this.loadMethodsAPI()
            }
        }
        
        private loadMethodsAPI() {
            this.initialising = true
            this.status = ''
            this.showError = false
            this.methodsService.getMethodsAPI().then(
                (data: ISwagger) => this.handleAPIData(data),
                (error: any) => this.handleAPIError(error)
            )
        }
        
        private handleAPIData(data: ISwagger) {
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
        private handleServiceData(data: IResponse, path: string) {
            //console.log('success: '+JSON.stringify(data.message))
            // new result id
            var id = this.userService.createId()
            this.userService.user.results.push(new Result(this.database.id, id, path, data))
            // refresh localStorage
            this.userService.localStorage.results = this.userService.user.results
            this.scope.$broadcast('update-results', id)
            this.methodsService.status = 'Added service result to user data'
            // system notification
            this.methodsService.notify('success', this.database.id)
        }
        
        private handleServiceError(error: any) {
            //console.log('failure: '+error.status)
            if(error.status == 404) {
                this.methodsService.status = error.status+' resource not found'
            } else if(error.status == 500) {
                this.methodsService.status = error.status+' internal server error'   
            } else {
                var response = <IResponse>error.data
                this.methodsService.status = response.message
            }
            // system notification
            this.methodsService.notify('error', this.database.id)
        }
        
        // method for submission
        public submitMethod() {
            //console.log('submitted '+this.currentMethod.path+' '+this.request['id'])
            this.methodsService.status = 'Loading data from service'
            // system notification
            this.methodsService.notify('loading', this.database.id)

            var currentMethod = this.methods.filter(
                   (m) => m.path == this.userService.sessionStorage.methods[this.database.id].path)[0]
            var currentRequest = this.userService.sessionStorage.methods[this.database.id].params
            
            var httpMethod = currentMethod.operations[0].method
            if(httpMethod == 'GET') {
                this.methodsPromise = 
                    this.methodsService.requestMethod(currentMethod.path, currentRequest).get().$promise   
               this.methodsPromise.then(
                    (data: IResponse) => this.handleServiceData(data, currentMethod.path),
                    (error: any) => this.handleServiceError(error))
                // only getVOTableURL atm
            } else if(httpMethod == 'POST') {
                this.methodsPromise = 
                    this.methodsService.requestMethod(currentMethod.path).save(currentRequest).$promise 
                this.methodsPromise.then(
                    (data: IResponse) => this.handleServiceData(data, currentMethod.path),
                    (error: any) => this.handleServiceError(error))
            }                  
        }
        
        // notifies dir to reset the request
        public resetMethod() {
            this.scope.$broadcast('reset-method-request')
        }
        
        // retry if alert is cancelled
        public retry() {
            this.loadMethodsAPI()
        }
        
        // helpers for methods modal
        public dropdownStatus = {
            isopen: false,
            active: 'Choose Method'
        }
        
        // set a method active and forward info to directives
        public setActive(method: Api) {
            //console.log('set-active')
            this.dropdownStatus.active = this.trimPath(method.path)
            // here we need a delay (maybe we shift this somewhere else)
            this.timeout(() => this.scope.$broadcast('set-method-active', method))
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
        
        // method for modal
        public saveMethods(save: boolean) {
            if(save)
                this.modalInstance.close()
            else
                this.modalInstance.dismiss()
            this.showError = false
        }

        
    }
}