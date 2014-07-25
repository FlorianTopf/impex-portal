/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IMethodsScope extends ng.IScope {
        methvm: MethodsCtrl;
    }

    // @TODO introduce error/offline handling later
    export class MethodsCtrl {
        private scope: portal.IMethodsScope
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private window: ng.IWindowService
        private configService: portal.ConfigService
        private methodsService: portal.MethodsService
        private userService: portal.UserService 
        private state: ng.ui.IStateService
        private modalInstance: any
        private methodsPromise: ng.IPromise<any>
        
        public database: Database = null
        public methods: Array<Api>
        public initialising: boolean = false
        public status: string = ''
        public showError: boolean = false  
        public currentMethod: Api
        public request: Object = {}
        
        static $inject: Array<string> = ['$scope', '$location', '$timeout', '$window',
            'configService', 'methodsService', 'userService', '$state', '$modalInstance', 'id']

        constructor($scope: IMethodsScope, $location: ng.ILocationService, $timeout: ng.ITimeoutService, 
            $window: ng.IWindowService, configService: portal.ConfigService, 
            methodsService: portal.MethodsService, userService: portal.UserService,
            $state: ng.ui.IStateService, $modalInstance: any, id: string) {   
            this.scope = $scope
            $scope.methvm = this
            this.location = $location
            this.timeout = $timeout   
            this.window = $window
            this.configService = configService
            this.methodsService = methodsService
            this.userService = userService
            this.state = $state
            this.modalInstance = $modalInstance
            
            this.database = this.configService.getDatabase(id)
            
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
            this.status = ''
            this.methodsService.getMethodsAPI().get(
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
            console.log('success: '+data.message)
            
            this.methodsService.loading = false
            this.methodsService.status = 'success'
            
            // @TODO we change id creation later
            var id = this.userService.createId()
            
            // @TODO we must take care of custom results (e.g. getMostRelevantRun)
            this.userService.user.results.push(new Result(id, this.currentMethod.path, data))
            //refresh localStorage
            this.userService.localStorage.results = this.userService.user.results
            
            this.scope.$broadcast('update-results', id)
        }
        
        private handleServiceError(error: any) {
            console.log('failure: '+error.status)

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
            console.log('submitted '+this.currentMethod.path+' '+this.request['id'])
            
            this.methodsService.loading = true
            this.methodsService.status = ''
            this.methodsService.showError = false
            
            this.methodsService.requestMethod(this.currentMethod.path, this.request).get(
                (data: IResponse, status: any) => this.handleServiceData(data, status),
                (error: any) => this.handleServiceError(error)
            )
        }
        
        
        // helpers for methods modal
        public dropdownStatus = {
            isopen: false,
            active: 'Choose Method'
        }
        
        //@TODO move this to directive later
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