/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IConfigScope extends ng.IScope {
        vm: ConfigCtrl;
    }

    export class ConfigCtrl {        
        private location: ng.ILocationService
        private window: ng.IWindowService
        private configService: portal.ConfigService
        private userService: portal.UserService
        
        public status: string
        public showError: boolean = false  

        static $inject: Array<string> = ['$scope', '$location', '$window', 'configService', 'userService']

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IConfigScope, $location: ng.ILocationService,
            $window: ng.IWindowService, configService: portal.ConfigService, userService: portal.UserService) {   
            $scope.vm = this
            this.location = $location
            this.window = $window  
            this.configService = configService  
            this.userService = userService
                
            this.load()
        }
        
        public retry() {
            this.showError = false
            this.load()
        }

        private load() {
              this.configService.getConfig().get({fmt: 'json' },
                  (data: any, status: any) => this.handleData(data, status), 
                  (error: any) => this.handleError(error)    
              )
        }

        private handleData(data: any, status: any) {
            this.status = "success"
            this.configService.config = <IConfig>data.impexconfiguration
            // @TODO this might come from the server in the future
            this.userService.user = new User(this.userService.createId())
            if(this.configService.config && this.userService.user)
                 this.location.path('/portal')
            else 
                this.handleError(data)
        }

        private handleError(error: any) {
            console.log("config error")
            if(this.window.confirm('connection timed out. retry?'))
                this.load()
            else {
                this.showError = true
                var error = <IResponse>error.data
                this.status = error.message
            }
        }        

    }
}