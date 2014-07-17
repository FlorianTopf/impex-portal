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

    export class UserDataDir implements ng.IDirective {

        public injection(): any[] {
            return [
                'userService',
                (userService: portal.UserService) => 
                    { return new UserDataDir(userService); }
            ]
        }

        public link: ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        public loading: boolean = false
        public status: string
        public showError: boolean = false  
        public isCollapsed: ICollapsedMap = {}
        // current resource selections which are fully displayed
        public currentSelection: Array<Selection> = []

        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private userService: portal.UserService
        private user: User
        private myScope: ng.IScope

        constructor(userService: portal.UserService) {
            this.userService = userService
            // @FIXME refactor this template (it's really ugly atm)
            this.templateUrl = '/public/partials/templates/userdata.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
          
        }

        linkFn($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.userdirvm = this
            this.myScope = $scope
            this.user = this.userService.user
            
            //collapsing all selections on init
            if(this.userService.user.selections) {
                this.userService.user.selections.map((e) => {
                    this.isCollapsed[e.id] = true
                })
            }
            
            //collapsing all results on init
            if(this.userService.user.results) {
               //for(var id in this.userService.user.results)
               //    this.isCollapsed[id] = true
               this.userService.user.results.map((e) => {
                    this.isCollapsed[e.id] = true
               })
            }
            
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
                // just for the moment (collapse all on enter)
                for(var id in this.isCollapsed)
                   this.isCollapsed[id] = true
                // just for the moment (reset expanded selections)    
                this.currentSelection = []
            })
        }
        
        public toggleResultDetails(id: string) {
            if(this.isCollapsed[id]) { // if it is closed
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
            } else {
                this.isCollapsed[id] = true
            }
        }
        
        public toggleSelectionDetails(id: string) {
            if(this.isCollapsed[id]) { // if it is closed 
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                this.currentSelection.push(
                    this.user.selections.filter((e) => e.id == id)[0])
            } else {
                this.isCollapsed[id] = true
                this.currentSelection = 
                    this.currentSelection.filter((e) => e.id != id)
            }
        }
        
        public deleteResult(id: string) {
            this.user.results = this.user.results.filter((e) => e.id != id)
            // update localStorage
            this.userService.localStorage.results = this.user.results
            // delete collapsed info
            delete this.isCollapsed[id]
        }
        
        public deleteSelection(id: string) {
            this.user.selections = this.user.selections.filter((e) => e.id != id)
            // update localStorage
            this.userService.localStorage.selections = this.user.selections
            // savely clear currentSelection => maybe we don't need that
            this.currentSelection = []
            // delete collapsed info
            delete this.isCollapsed[id]
        }
        
        
    }

}