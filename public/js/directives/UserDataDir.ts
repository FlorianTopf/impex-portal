/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IUserDataDirScope extends ng.IScope {
       userdirvm: UserDataDir
    }
    
    // collapsed view map
    export interface ICollapsedMap {
        [id: string]: boolean
    }

    export class UserDataDir {

        public injection(): any[] {
            return [
                '$timeout',
                'configService',
                'userService',
                ($timeout: ng.ITimeoutService, configService: portal.ConfigService, userService: portal.UserService) => 
                    { return new UserDataDir($timeout, configService, userService); }
            ]
        }

        public link: ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        public loading: boolean = false
        public status: string
        public showError: boolean = false  

        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private userService: portal.UserService
        private user: User
        private myScope: ng.IScope
        private isCollapsed: ICollapsedMap = {}

        constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService, userService: portal.UserService) {
            this.timeout = $timeout
            this.configService = configService
            this.userService = userService
            this.user = this.userService.user
            this.templateUrl = '/public/partials/templates/userdata.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
            
        }

        linkFn($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.userdirvm = this
            this.myScope = $scope
            
            this.myScope.$on('update-user-data', (e, id: string) => {
                this.loading = false
                this.isCollapsed[id] = true
            })
            
            this.myScope.$on('handle-service-error', (e, msg: string) => {
                this.loading = false
                this.showError = true
                this.status = msg
            })
            
            this.myScope.$on('load-service-data', (e) => {
                this.showError = false
                this.loading = true
            })
        }
        
        public showDetails(id: string) {
            this.isCollapsed[id] = !this.isCollapsed[id]
        
        }
        
        public validateUrl(str) {
            var pattern = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/
            if(!pattern.test(str)) {
                return false;
            } else {
                return true;
            }
        }
        
    }

}