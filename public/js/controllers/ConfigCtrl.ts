/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IConfigScope extends ng.IScope {
        vm: ConfigCtrl;
    }

    export class ConfigCtrl {        
        private configService: portal.ConfigService
        private userService: portal.UserService
        private state: ng.ui.IStateService

        static $inject: Array<string> = ['$scope', 'configService', 'userService', '$state', 'config']

        constructor($scope: IConfigScope, configService: portal.ConfigService, 
            userService: portal.UserService, $state: ng.ui.IStateService, config: IConfig) {   
            this.configService = configService  
            this.userService = userService
            this.state = $state   

            this.configService.config = config
            
            // @TODO this comes from the server in the future (add in resolver)
            this.userService.user = new User(this.userService.createId())
            
            
        }
        
        
    }
}