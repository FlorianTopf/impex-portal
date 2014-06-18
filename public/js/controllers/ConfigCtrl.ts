/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IConfigScope extends ng.IScope {
        vm: ConfigCtrl;
    }

    export class ConfigCtrl {

        public status: string
        public showError: boolean = false
        
        private configService: portal.ConfigService

        private http: ng.IHttpService
        private location: ng.ILocationService
        private window: ng.IWindowService

        static $inject: Array<string> = ['$scope', '$http', '$location', '$window', 'configService']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IConfigScope, $http: ng.IHttpService, $location: ng.ILocationService,
            $window: ng.IWindowService, configService: portal.ConfigService) {   
            $scope.vm = this
            this.configService = configService  
            this.location = $location
            this.http = $http
            this.window = $window   
                
            this.load()
        }
        
        public retry() {
            this.showError = false
            this.load()
        }

        private load() {
              this.configService.getConfig().get({fmt: 'json' },
                  (data: any, status: any) => this.handleData(data, status), 
                  (data: any, status: any) => this.handleError(data, status)    
              )
        }

        private handleData(data: any, status: any) {
            this.status = "success"
            this.configService.config = <IConfig>data.impexconfiguration
            if(this.configService.config)
                 this.location.path('/portal')
            else 
                this.handleError(data, status)
        }

        private handleError(data: any, status: any) {
            console.log("config error")
            if(this.window.confirm('connection timed out. retry?'))
                this.load()
            else {
                this.showError = true
                this.status = data+" "+status
            }
        }        

    }
}