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
        public isSelApplyable: boolean = false
        public isVOTApplyable: boolean = false
        // for SINP models/outputs
        public applyableModel: string = null
        // active tabs (first by default)
        public tabsActive: Array<boolean> = []

        private userService: portal.UserService
        private state: ng.ui.IStateService
        private user: User
        private myScope: ng.IScope

        constructor(userService: portal.UserService, $state: ng.ui.IStateService) {
            this.userService = userService
            this.state = $state
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
            // for SINP models/outputs
            this.applyableModel = null
            
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
               this.isSelApplyable = false
               if(this.currentSelection.length == 1) {
                    this.applyableElements.forEach((e) => {
                        console.log("Element "+e.trim())
                        if(this.currentSelection[0].type == e.trim())
                            this.isSelApplyable = true  
                    })
               } 
            })
            
            // comes from MethodsCtrl
            this.myScope.$on('set-applyable-votable', (e, b: boolean) => this.isVOTApplyable = b)
            
            // comes from MethodsCtrl => needed for SINP models/output
            this.myScope.$on('set-applyable-models', (e, m: string) => { 
                console.log(m)
                this.applyableModel = m 
                if(this.currentSelection.length == 1) {
                    var elem = this.currentSelection[0]
                    var output = this.applyableModel.replace('SimulationModel', 'NumericalOutput')
                    var index = elem.elem.resourceId.indexOf(output.replace('OnFly', ''))
                    if(this.applyableModel == elem.elem.resourceId || index != -1)
                        this.isSelApplyable = true
                    else
                        this.isSelApplyable = false
                }      
            })
            
            // comes from RegistryCtrl
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
            
            // comes from UserDataCtrl
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
            
            // comes from MethodsCtrl
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
                this.isSelApplyable = false
                this.isVOTApplyable = false
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
                
                // get selection
                var selection = this.user.selections.filter((e) => e.id == id)[0]
                if(this.applyableElements.indexOf(selection.type) != -1) {
                    this.isSelApplyable = true
                    //console.log("isApplyable")  
                    // here we check SINP models/outputs
                    //console.log(this.applyableModel+" "+selection.elem.resourceId)
                    if(this.applyableModel) {
                        var output = this.applyableModel.replace('SimulationModel', 'NumericalOutput')
                        var index = selection.elem.resourceId.indexOf(output.replace('OnFly', ''))
                        if(this.applyableModel == selection.elem.resourceId || index != -1)
                            this.isSelApplyable = true
                        else
                            this.isSelApplyable = false
                    }  
                }
                this.currentSelection.push(selection)
                
            } else {
                this.isCollapsed[id] = true
                // set isApplyable to false
                this.isSelApplyable = false
            }
        }
        
        public toggleDetails(id: string) {
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
        
        public deleteVOTable(vot: IUserData) {
            // update local votables
            this.user.voTables = this.user.voTables.filter((e) => e.id != vot.id)
            // update global service
            this.userService.user.voTables = 
                this.userService.user.voTables.filter((e) => e.id != vot.id)
            // delete file on server
            this.userService.deleteUserData(vot.url.split('/').reverse()[0])
            // delete collapsed info
            delete this.isCollapsed[vot.id]
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