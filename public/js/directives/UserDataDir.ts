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
                '$state',
                (userService: portal.UserService, $state: ng.ui.IStateService) => 
                    { return new UserDataDir(userService, $state); }
            ]
        }

        public link: ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        public repositoryId: string = null
        public isCollapsed: ICollapsedMap = {}
        // current resource selection which is fully displayed
        public currentSelection: Array<Selection> = []
        // currently applyable elements (according to current method)
        public applyableElements: Array<string> = []
        public isApplyable: boolean = false
        // active tabs (first by default)
        public tabsActive: Array<boolean> = []

        private userService: portal.UserService
        private stateService: ng.ui.IStateService
        private user: User
        private myScope: ng.IScope

        constructor(userService: portal.UserService, $state: ng.ui.IStateService) {
            this.userService = userService
            this.stateService = $state
            this.templateUrl = '/public/partials/templates/userdataDir.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
          
        }

        linkFn($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.userdirvm = this
            this.myScope = $scope
            this.user = this.userService.user
            this.tabsActive = [true, false, false] // selections, votables, results
            
            attributes.$observe('db', (id?: string)  => { 
                this.repositoryId = id
            })
            
            // collapsing all selections on init
            if(this.user.selections) {
                this.user.selections.map((e) => {
                    this.isCollapsed[e.id] = true
                })
            }
            
            // collapsing all votables on init
            if(this.user.voTables) {
                this.user.voTables.map((e) => {
                    this.isCollapsed[e.id] = true
                })
            }
            
            // collapsing all results on init
            if(this.user.results) {
               this.user.results.map((e) => {
                    this.isCollapsed[e.id] = true
               })
            }
            
            // comes from MethodsCtrl
            this.myScope.$on('set-applyable-elements', (e, elements: string) => {
               this.applyableElements = elements.split(',')
               this.isApplyable = false
               if(this.currentSelection.length == 1) {
                    this.applyableElements.forEach((e) => {
                        console.log("Element "+e)
                        if(this.currentSelection[0].type === e)
                            this.isApplyable = true  
                    })
               } 
            })
            
            this.myScope.$on('update-selections', (e, id: string) => {
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                // reset expanded selection
                this.currentSelection = []
                this.currentSelection.push(this.user.selections.filter((e) => e.id == id)[0])
                // set selections tab active
                this.tabsActive = this.tabsActive.map((t) => t = false)
                this.tabsActive[0] = true
            })
            
            // @TODO finalise this!
            this.myScope.$on('update-votables', (e, id: string) => {
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                // reset expanded selection
                this.currentSelection = []
                // set votable tab active
                this.tabsActive = this.tabsActive.map((t) => t = false)
                this.tabsActive[1] = true
            
            })
            
            this.myScope.$on('update-results', (e, id: string) => {
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                // reset expanded selection
                this.currentSelection = []
                // set result tab active
                this.tabsActive = this.tabsActive.map((t) => t = false)
                this.tabsActive[2] = true
            })
            
            // watch event when all content is loading into the dir
            this.myScope.$watch('$includeContentLoaded', (e) => {                      
                // collapse all on enter
                for(var id in this.isCollapsed)
                   this.isCollapsed[id] = true
                // reset expanded selections  
                this.currentSelection = []
                // reset also applyables
                this.applyableElements = []
                this.isApplyable = false
            })
        }
         
        public toggleSelectionDetails(id: string) {
            // reset expanded selection
            this.currentSelection = []
            if(this.isCollapsed[id]) { // if it is closed 
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                
                var selection = this.user.selections.filter((e) => e.id == id)[0]
                if(this.applyableElements.indexOf(selection.type) != -1) {
                    this.isApplyable = true
                    //console.log("isApplyable")  
                }
                this.currentSelection.push(selection)
                
            } else {
                this.isCollapsed[id] = true
                // set isApplyable to false
                this.isApplyable = false
            }
        }
        
        public toggleVOTableDetails(id: string) {
            // reset expanded selection
            this.currentSelection = []
            // reset expanded selection
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
        
        public toggleResultDetails(id: string) {
            // reset expanded selection
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
          
        public deleteSelection(id: string) {
            // update local selections
            this.user.selections = this.user.selections.filter((e) => e.id != id)
            // update global service/localStorage
            this.userService.user.selections = 
                this.userService.user.selections.filter((e) => e.id != id)
            this.userService.localStorage.selections = this.userService.user.selections 
            // safely clear currentSelection
            this.currentSelection = []
            // delete collapsed info
            delete this.isCollapsed[id]
        }
 		
        public deleteResult(id: string) {
            // update local selection
            this.user.results = this.user.results.filter((e) => e.id != id)
            // update global service/localStorage
            this.userService.user.results = 
                this.userService.user.results.filter((e) => e.id != id)
            this.userService.localStorage.results = this.userService.user.results
            // delete collapsed info
            delete this.isCollapsed[id]
        }
        
        
    }

}