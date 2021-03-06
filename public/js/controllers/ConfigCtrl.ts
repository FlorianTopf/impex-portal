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
            // only use those of the config which are enabled for the portal
            this.configService.config.databases = config.databases.filter((d) => d.portal)
            // read all regions at startup
            this.configService.filterRegions = regions.data
            
            // map only for databses with portal flag equal true
            this.configService.config.databases
                .filter((e) => e.portal == true)
                .forEach((e) => { 
                    // initialise portal map disablers
                    this.configService.aliveMap[e.id] = false
                    /*  this.configService.aliveMap[e.id] = true */
                    this.configService.filterMap[e.id] = true
                    // calling isAlive
                    this.configService.isAlive(e) })
            
            // set interval to check if methods are still alive => every 10 minutes (600k ms)
            this.interval(() => this.configService.config.databases
                .filter((e) => e.portal == true)
                .forEach((e) => { 
                    this.configService.isAlive(e) }), 600000)
            
            // only local user atm (could be resolved from the server in the future) 
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