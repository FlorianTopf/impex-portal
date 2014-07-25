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
            this.templateUrl = '/public/partials/templates/userdataDir.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
          
        }

        linkFn($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.userdirvm = this
            this.myScope = $scope
            this.user = this.userService.user
            
            // collapsing all selections on init
            if(this.userService.user.selections) {
                this.userService.user.selections.map((e) => {
                    this.isCollapsed[e.id] = true
                })
            }
            
            // collapsing all results on init
            if(this.userService.user.results) {
               this.userService.user.results.map((e) => {
                    this.isCollapsed[e.id] = true
               })
            }
            
            this.myScope.$on('update-selections', (e, id: string) => {
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                this.currentSelection.push(
                    this.user.selections.filter((e) => e.id == id)[0])

            })
            
            this.myScope.$on('update-results', (e, id: string) => {
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                // just for the moment (reset expanded selections)
                this.currentSelection = []
            })
            
            // watch event when all content is loading into the dir
            this.myScope.$watch('$includeContentLoaded', (e) => {                      
                // just for the moment (collapse all on enter)
                for(var id in this.isCollapsed)
                   this.isCollapsed[id] = true
                // just for the moment (reset expanded selections)    
                this.currentSelection = []
            })
        }
        
        public toggleResultDetails(id: string) {
            // just for the moment (reset expanded selections)
            this.currentSelection = []
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
                this.currentSelection = []
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
            // savely clear currentSelection
            this.currentSelection = []
            // delete collapsed info
            delete this.isCollapsed[id]
        }
        
        
    }

}