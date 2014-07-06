/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IConfigScope extends ng.IScope {
        vm: ConfigCtrl;
    }

    export class ConfigCtrl {        
        private timeout: ng.ITimeoutService
        private scope: portal.IConfigScope
        private configService: portal.ConfigService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        
        //public ready: boolean = false
        //public status: string
        //public showError: boolean = false 

        static $inject: Array<string> = ['$scope', '$timeout', 'configService', 'userService', '$state', 'config']

        constructor($scope: IConfigScope, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            userService: portal.UserService, $state: ng.ui.IStateService, config: IConfig) {  
            this.scope = $scope
            this.scope.vm = this 
            this.timeout = $timeout
            this.configService = configService  
            this.userService = userService
            this.state = $state   

            this.configService.config = config
            
            // @TODO this comes from the server in the future (add in resolver)
            this.userService.user = new User(this.userService.createId())
              
        }

      
        
    }
}