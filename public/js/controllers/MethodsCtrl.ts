/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IMethodsScope extends ng.IScope {
        methvm: MethodsCtrl;
    }

    export class MethodsCtrl {
        private scope: portal.IMethodsScope
        private http: ng.IHttpService
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private window: ng.IWindowService
        private configService: portal.ConfigService
        private methodsService: portal.MethodsService
        private modalInstance: any
        private database: Database
        private methodsPromise: ng.IPromise<any>
        private methods: Array<Api>
        //private initialising: boolean = false
        //private loading: boolean = false
        //private transFinished: boolean = true
  
        public status: string
        public showError: boolean = false  
        public currentMethod: Api
        public request: Object = {}
        
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout', '$window',
            'configService', 'methodsService', '$modalInstance', 'database']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IMethodsScope, $http: ng.IHttpService, $location: ng.ILocationService,
            $timeout: ng.ITimeoutService, $window: ng.IWindowService, configService: portal.ConfigService, 
            methodsService: portal.MethodsService, $modalInstance: any, database: Database) {   
            this.scope = $scope
            $scope.methvm = this
            this.configService = configService
            this.methodsService = methodsService
            this.location = $location
            this.http = $http
            this.timeout = $timeout   
            this.window = $window
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
            this.methodsService.getMethodsAPI().get(
                (data: ISwagger, status: any) => this.handleData(data, status),
                (data: any, status: any) => this.handleError(data, status)
            )
        }
        
        private handleData(data: ISwagger, status?: any) {
            this.status = "success"
            // we always get the right thing
            this.methodsService.methods = data
            this.methods = this.methodsService.getMethods(this.database)
        } 
        
        private handleError(data: any, status: any) {
            console.log("config error")
            if(this.window.confirm('connection timed out. retry?'))
                this.loadMethodsAPI()
            else {
                this.showError = true
                this.status = data+" "+status
            }
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
            console.log("submitted "+this.currentMethod.path+" "+this.request['id'])
        
        }
        
        // testing methods for modal
        public methodsOk() {
            this.modalInstance.close()

        }
        
        public methodsCancel() {
            this.modalInstance.dismiss()

        }
    

    }
}