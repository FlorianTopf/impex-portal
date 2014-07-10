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
                'configService',
                'userService',
                (configService: portal.ConfigService, userService: portal.UserService) => 
                    { return new UserDataDir(configService, userService); }
            ]
        }

        public link: ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        public loading: boolean = false
        public status: string
        public showError: boolean = false  
        public isCollapsed: ICollapsedMap = {}
        // current resource selection which is fully displayed
        public currentSelection: Selection = null

        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private userService: portal.UserService
        private user: User
        private myScope: ng.IScope

        constructor(configService: portal.ConfigService, userService: portal.UserService) {
            this.configService = configService
            this.userService = userService
            this.templateUrl = '/public/partials/templates/userdata.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
            
        }

        linkFn($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.userdirvm = this
            this.myScope = $scope
            this.user = this.userService.user
            
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
            
            this.myScope.$on('clear-service-error', (e) => {
                this.showError = false
                this.status = ''
            })
            
            // we need to watch on the modal => how we can achieve this?
            this.myScope.$watch('$includeContentLoaded', (e) => {
                console.log("UserDataDir loaded") 
                // just for the moment
                for(var elem in this.isCollapsed)
                   this.isCollapsed[elem] = true
            })
        }
        
        public toggleDetails(id: string) {
            this.isCollapsed[id] = !this.isCollapsed[id]
            // should always return only one
            //this.currentSelection = this.user.selections.filter((e) => e.id == id)[0]
        }
        
        public validateUrl(str: string): boolean {
            var pattern = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/
            if(!pattern.test(str)) {
                return false;
            } else {
                return true;
            }
        }
        
        
        public beautify(str: string) {
            var array = str.match(/([A-Z]?[^A-Z]*)/g).slice(0,-1)
            var first = array[0].charAt(0).toUpperCase()+array[0].slice(1)
            array.shift()
            return (first+" "+array.join(" ")).trim()
        }
        
        public typeOf(thing: any): string {
            switch(typeof thing){
                case "object":
                    if(Object.prototype.toString.call(thing) === "[object Array]"){
                        return 'array'
                    } else if (thing == null) {
                        return 'null'
                    } else if(Object.prototype.toString.call(thing) === "[object Object]"){
                        return 'object'
                    }
                case "string":
                    if(this.validateUrl(thing)){
                        return 'url'
                    } else {
                        return 'string'
                    }
                default:
                   return typeof thing
            }
        }
            
        
    }

}