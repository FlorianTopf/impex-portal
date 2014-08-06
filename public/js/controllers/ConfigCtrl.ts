/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IConfigScope extends ng.IScope {
        vm: ConfigCtrl;
    }

    export class ConfigCtrl {        
        private scope: portal.IConfigScope
        private interval: ng.IIntervalService
        private configService: portal.ConfigService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        
        //public ready: boolean = false
        //public status: string
        //public showError: boolean = false 

        static $inject: Array<string> = ['$scope', '$interval', 'configService', 'userService', '$state', 
            'config', 'userData']

        constructor($scope: IConfigScope, $interval: ng.IIntervalService, configService: portal.ConfigService, 
            userService: portal.UserService, $state: ng.ui.IStateService, config: IConfig, userData: Array<IUserData>) {  
            this.scope = $scope
            this.scope.vm = this 
            this.interval = $interval
            this.configService = configService  
            this.userService = userService
            this.state = $state   

            this.configService.config = config
            
            // only for simulations atm 
            this.configService.config.databases
                .filter((e) => e.type == 'simulation')
                .map((e) => { 
                    this.configService.aliveMap[e.name] = false 
                    this.configService.isAlive(e.name) })
            
            // @TODO this routine must be changed (if we use filters in parallel)
            //set interval to check if methods are still alive => every 10 minutes (600k ms)
            this.interval(() => this.configService.config.databases
                .filter((e) => e.type == 'simulation')
                .map((e) => { 
                    //this.configService.aliveMap[e.name] = false 
                    this.configService.isAlive(e.name) }), 600000)
            
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