/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IConfigScope extends ng.IScope {
        vm: ConfigCtrl;
    }

    export class ConfigCtrl {        
        private scope: portal.IConfigScope
        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        
        //public ready: boolean = false
        //public status: string
        //public showError: boolean = false 

        static $inject: Array<string> = ['$scope', '$timeout', 'configService', 'userService', '$state', 
            'config', 'userData']

        constructor($scope: IConfigScope, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            userService: portal.UserService, $state: ng.ui.IStateService, config: IConfig, userData: Array<IUserData>) {  
            this.scope = $scope
            this.scope.vm = this 
            this.timeout = $timeout
            this.configService = configService  
            this.userService = userService
            this.state = $state   

            this.configService.config = config
            
            // user info comes from the server in the future (add in resolver too) 
            this.userService.user = new User(this.userService.createId())
            
            // loading stored votables from server
            if(userData.length > 0)
                this.userService.user.voTables = userData
            //console.log(JSON.stringify(this.userService.user.voTables))
            
            //var name = "votable-53dfe33c3004d0ef2d6f570e.xml"
            //this.userService.UserData().delete({}, {'name': name})
            
            // as soon as we create a new user we have a localStorage connection
            // initialising the stored results from localStorage
            if(this.userService.localStorage.results != null)
                this.userService.user.results = this.userService.localStorage.results
                
            if(this.userService.localStorage.selections != null)
                this.userService.user.selections = this.userService.localStorage.selections
            
              
        }
      
        
    }
}