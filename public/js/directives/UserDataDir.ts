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
                'registryService',
                'userService',
                '$state',
                (registryService: portal.RegistryService, userService: portal.UserService, $state: ng.ui.IStateService) => 
                    { return new UserDataDir(registryService, userService, $state); }
            ]
        }

        public link: ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        public repositoryId: string = null
        public isCollapsed: ICollapsedMap = {}
        // currently applyable elements (according to current method)
        public applyableElements: Array<string> = []
        public isSelApplyable: boolean = false
        public isVOTApplyable: boolean = false
        // for SINP models/outputs
        public applyableModel: string = null
        // active tabs (first by default)
        public tabsActive: Array<boolean> = []
        public selectables: Array<string> = []

        private registryService: portal.RegistryService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        private user: User
        private myScope: ng.IScope

        constructor(registryService: portal.RegistryService, userService: portal.UserService, $state: ng.ui.IStateService) {
            this.registryService = registryService
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
            
            attributes.$observe('db', (id?: string)  => { 
                this.selectables = this.registryService.selectables[id]
                this.repositoryId = id
            })
            
            // watch event when all content is loaded into the dir
            this.myScope.$watch('$includeContentLoaded', (e) => {          
                //console.log("UserDataDir loaded")     
                // collapsing all selections on init
                if(this.user.selections) {
                    this.user.selections.forEach((e) => {
                        this.isCollapsed[e.id] = true
                    })
                }
                // collapsing all votables on init
                if(this.user.voTables) {
                    this.user.voTables.forEach((e) => {
                        this.isCollapsed[e.id] = true
                    })
                }
                // collapsing all results on init
                if(this.user.results) {
                    this.user.results.forEach((e) => {
                        this.isCollapsed[e.id] = true
                    })
                }
                // reset expanded selections  
                this.user.activeSelection = []
                // reset applyables
                this.applyableElements = []
                this.applyableModel = null   
                // init tabs
                this.tabsActive = [true, false, false] // selections, votables, results
                this.isSelApplyable = false
                this.isVOTApplyable = false
            })
            
            // comes from MethodsCtrl
            this.myScope.$on('set-applyable-elements', (e, elements: string) => {
               //console.log('set-applyable-elements')
               this.applyableElements = elements.split(',').map((e) => e.trim())
               this.isSelApplyable = false
               if(this.user.activeSelection.length == 1) {
                    this.applyableElements.forEach((e) => {
                        //console.log("Element "+e)
                        if(this.user.activeSelection[0].type == e)
                            this.isSelApplyable = true  
                    })
               }
            })
            
            // comes from MethodsCtrl
            this.myScope.$on('set-applyable-votable', (e, b: boolean) => { 
                //console.log('set-applyable-votable')
                this.isVOTApplyable = b
            })
            
            // comes from MethodsCtrl => needed for SINP models/output
            this.myScope.$on('set-applyable-models', (e, m: string) => { 
                //console.log(m)
                //console.log('set-applyable-models')
                this.applyableModel = m 
                if(this.user.activeSelection.length == 1) {
                    var element = this.user.activeSelection[0]
                    var output = this.applyableModel.replace('SimulationModel', 'NumericalOutput')
                    var index = element.elem.resourceId.indexOf(output.replace('OnFly', ''))
                    if(this.applyableModel == element.elem.resourceId || index != -1)
                        this.isSelApplyable = true
                    else
                        this.isSelApplyable = false
                }      
            })
            
            // comes from RegistryDir
            this.myScope.$on('update-selections', (e, id: string) => {
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                // set selections tab active
                this.tabsActive = this.tabsActive.map((t) => t = false)
                this.tabsActive[0] = true
            })
            
            
            // comes from UserDataCtrl
            this.myScope.$on('update-votables', (e, id: string) => {
                // reset expanded selection
                this.user.activeSelection = []
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                // set votable tab active
                this.tabsActive = this.tabsActive.map((t) => t = false)
                this.tabsActive[1] = true
            
            })
            
            // comes from MethodsCtrl
            this.myScope.$on('update-results', (e, id: string) => {
                // reset expanded selection
                this.user.activeSelection = []
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                // set result tab active
                this.tabsActive = this.tabsActive.map((t) => t = false)
                this.tabsActive[2] = true
            })
            
        }
        
        public isSelectable(type: string): boolean {
            // hack for static SINP models => not selectable
            if(type == 'SimulationModel' && 
                this.repositoryId.indexOf('SINP') != -1) {
                // there is always only one
                if(this.user.activeSelection[0].elem.resourceId.indexOf('Static') != -1)
                    return false
                else
                    return true
            } else
                return this.selectables.indexOf(type) != -1
        }
        
        public isSelSaved(id: string): boolean {
            if(this.user.selections.filter((s) => s.id == id).length == 1)
                return true
            else
                return false
        }
        
        public saveSelection(type: string, elem: SpaseElem) { 
            // get active selection
            var selection = this.user.activeSelection.filter((e) => e.elem === elem)[0]
            this.isCollapsed[selection.id] = false
            // closing all other collapsibles
            for(var rId in this.isCollapsed) {
                if(rId != selection.id)
                    this.isCollapsed[rId] = true
            }
            
            this.userService.user.selections.push(selection)
            // refresh localStorage
            this.userService.localStorage.selections = this.userService.user.selections

            // set selections tab active
            this.tabsActive = this.tabsActive.map((t) => t = false)
            this.tabsActive[0] = true
        }
         
        public toggleSelectionDetails(id: string) {
            // reset expanded selection
            this.user.activeSelection = []
            if(this.isCollapsed[id]) { // if it is closed 
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id)
                        this.isCollapsed[rId] = true
                }
                
                // get selection
                var selection = this.user.selections.filter((e) => e.id == id)[0]
                //console.log(this.applyableElements)
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
                this.user.activeSelection = [selection]
                
            } else {
                this.isCollapsed[id] = true
                // set isApplyable to false
                this.isSelApplyable = false
            }
        }
        
        public toggleDetails(id: string) {
            // reset expanded selection
            this.user.activeSelection = []
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
            this.user.activeSelection = []
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