/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IModalScope extends ng.IScope {
        modvm: ModalCtrl;
    }

    export class ModalCtrl {

        public status: string
        public showError: boolean = false
        
        private configService: portal.ConfigService

        private http: ng.IHttpService
        private location: ng.ILocationService
        private window: ng.IWindowService
        public modalInstance: any

        static $inject: Array<string> = ['$scope', '$http', '$location', '$window', 'configService', '$modalInstance']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IModalScope, $http: ng.IHttpService, $location: ng.ILocationService,
            $window: ng.IWindowService, configService: portal.ConfigService, $modalInstance: any) {   
            $scope.modvm = this
            this.configService = configService  
            this.location = $location
            this.http = $http
            this.window = $window    
            this.modalInstance = $modalInstance
        }
        
        public ok() {
            this.modalInstance.close()
        }
        
        public cancel() {
            this.modalInstance.dismiss()
        }
        
    

    }
}