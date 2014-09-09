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

        static $inject: Array<string> = ['$scope', '$interval', 'configService', 'userService', '$state', 
            'config', 'userData', 'regions']

        constructor($scope: IConfigScope, $interval: ng.IIntervalService, configService: portal.ConfigService, 
            userService: portal.UserService, $state: ng.ui.IStateService, 
            config: IConfig, userData: Array<IUserData>, regions: ng.IHttpPromiseCallbackArg<Array<string>>) {  
            this.scope = $scope
            this.scope.vm = this 
            this.interval = $interval
            this.configService = configService  
            this.userService = userService
            this.state = $state   
            this.configService.config = config
            // read all regions at startup (@TODO set an interval for refresh?)
            this.configService.filterRegions = regions.data
            
            // map only for simulation databases atm 
            this.configService.config.databases
                .filter((e) => e.type == 'simulation')
                .forEach((e) => { 
                    // initialise portal map disablers
                    this.configService.aliveMap[e.id] = false 
                    this.configService.filterMap[e.id] = true
                    // calling isAlive
                    this.configService.isAlive(e) })
            
            // @TODO this routine must be changed (if we use filters in parallel)
            // set interval to check if methods are still alive => every 10 minutes (600k ms)
            this.interval(() => this.configService.config.databases
                .filter((e) => e.type == 'simulation')
                .forEach((e) => { 
                    this.configService.isAlive(e) }), 600000)
            
            // @TODO user info comes from the server in the future (add in resolver too) 
            this.userService.user = new User(this.userService.createId())
            
            // loading stored votables from server
            if(userData.length > 0)
                this.userService.user.voTables = userData
            
            // initialising the stored results from localStorage
            if(this.userService.localStorage.results != null)
                this.userService.user.results = this.userService.localStorage.results
                
            if(this.userService.localStorage.selections != null)
                this.userService.user.selections = this.userService.localStorage.selections
                
              
        }
      
        
    }
}