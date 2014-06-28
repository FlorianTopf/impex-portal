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
        private configService: portal.ConfigService
        private methodsService: portal.MethodsService
        private modalInstance: any
        private database: Database
        private methodsPromise: ng.IPromise<any>
        //private initialising: boolean = false
        //private loading: boolean = false
        //private transFinished: boolean = true
        
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout', 
            'configService', 'registryService', '$modalInstance', 'database']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IMethodsScope, $http: ng.IHttpService, $location: ng.ILocationService,
            $timeout: ng.ITimeoutService, configService: portal.ConfigService, methodsService: portal.MethodsService,
            $modalInstance: any, database: Database) {   
            this.scope = $scope
            $scope.methvm = this
            this.configService = configService
            this.methodsService = methodsService
            this.location = $location
            this.http = $http
            this.timeout = $timeout   
            this.modalInstance = $modalInstance
            this.database = database
            
            // watches changes of variable 
            //(is changed each time modal is opened)
            //this.scope.$watch('this.database', 
            //    () => { this.getRepository(database.id) })
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