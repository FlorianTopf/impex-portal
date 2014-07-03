/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IMethodsScope extends ng.IScope {
        methvm: MethodsCtrl;
    }

    export class MethodsCtrl {
        private scope: portal.IMethodsScope
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private window: ng.IWindowService
        private configService: portal.ConfigService
        private methodsService: portal.MethodsService
        private userService: portal.UserService 
        private modalInstance: any
        private database: Database
        private methodsPromise: ng.IPromise<any>
        private methods: Array<Api>
        private initialising: boolean = false
  
        public status: string
        public showError: boolean = false  
        public currentMethod: Api
        public request: Object = {}
        
        static $inject: Array<string> = ['$scope', '$location', '$timeout', '$window',
            'configService', 'methodsService', 'userService', '$modalInstance', 'database']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IMethodsScope, $location: ng.ILocationService, $timeout: ng.ITimeoutService, 
            $window: ng.IWindowService, configService: portal.ConfigService, 
            methodsService: portal.MethodsService, userService: portal.UserService,
            $modalInstance: any, database: Database) {   
            this.scope = $scope
            $scope.methvm = this
            this.location = $location
            this.timeout = $timeout   
            this.window = $window
            this.configService = configService
            this.methodsService = methodsService
            this.userService = userService
            this.modalInstance = $modalInstance
            this.database = database
            
            if(this.methodsService.methods)
                this.methods = this.methodsService.getMethods(this.database)
            else
                this.loadMethodsAPI()
        }
        
        public retry() {
            this.showError = false
            this.loadMethodsAPI()
        }
        
        private loadMethodsAPI() {
            this.initialising = true
            this.methodsService.getMethodsAPI().get(
                (data: ISwagger, status: any) => this.handleAPIData(data, status),
                (error: any) => this.handleAPIError(error)
            )
        }
        
        private handleAPIData(data: ISwagger, status?: any) {
            this.initialising = false
            this.status = "success"
            // we always get the right thing
            this.methodsService.methods = data
            this.methods = this.methodsService.getMethods(this.database)
        } 
        
        private handleAPIError(error: any) {
            this.initialising = false
            if(this.window.confirm('connection timed out. retry?'))
                this.loadMethodsAPI()
            else {
                this.showError = true
                if(error.status = 404)
                    this.status = error.status+" resource not found"
                else
                    this.status = error.data+" "+error.status

            }
        }  
        
        private handleServiceData(data: IResponse, status?: any) {
            console.log("Success: "+data.message)
            // @TODO we change id creation later
            var id = this.userService.createId()
            // @TODO we must take care of custom results (getMostRelevantRun)
            this.userService.user.results[id] = data
            this.scope.$broadcast('update-user-data', id)
        }
        
        // @TODO display error in the user interface
        private handleServiceError(error: any) {
            console.log("Failure: "+error.status)
            if(error.status == 404)
                var message = error.status+" resource not found"
            else {
                var response = <IResponse>error.data
                var message = response.message
            }
            this.scope.$broadcast('handle-service-error', message)
        }
        
        // helpers for methods modal
        public dropdownStatus = {
            isopen: false,
            active: "Choose Method"
        }
        
        public setActive(method: Api) {
            this.dropdownStatus.active = this.trimPath(method.path)
            this.currentMethod = method
            // @TODO there is for now only one operation per method
             this.currentMethod.operations[0].parameters.forEach((p) => {
                   this.request[p.name] = p.defaultValue
               })
            
        }
        
        public isActive(path: string): boolean {
            return this.dropdownStatus.active === this.trimPath(path)
        }
        
        public getActive(): string {
            return this.dropdownStatus.active
        }
        
        public trimPath(path: string): string {
            var splitPath = path.split('/').reverse()
            return splitPath[0]
        }
        
        // testing method for submission
        public methodsSubmit() {
            this.scope.$broadcast('load-service-data')
            console.log("submitted "+this.currentMethod.path+" "+this.request['id'])
            this.methodsService.requestMethod(this.currentMethod.path, this.request).get(
                (data: IResponse, status: any) => this.handleServiceData(data, status),
                (error: any) => this.handleServiceError(error)
            )
        
        }
        
        // testing methods for modal
        public methodsSave() {
            this.modalInstance.close()
            this.scope.$broadcast('clear-service-error')

        }
        
        public methodsCancel() {
            this.modalInstance.dismiss()
            this.scope.$broadcast('clear-service-error')
        }
    

    }
}