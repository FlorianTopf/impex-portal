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
                'methodsService',
                'userService',
                '$state',
                'growl',
                (registryService: portal.RegistryService, methodsService: portal.MethodsService,
                userService: portal.UserService, $state: ng.ui.IStateService, growl: any) => 
                    { return new UserDataDir(registryService, methodsService, userService, $state, growl); }
            ]
        }

        public link: ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        public repositoryId: string = null
        public isCollapsed: ICollapsedMap = {}
        public selectables: Array<string> = []
        // currently applyable elements (actual method)
        public applyableElements: Array<string> = []
        // for SINP models/outputs
        public applyableModel: string = null
        public isSelApplyable: boolean = false
        public isVOTApplyable: boolean = false
        // active tabs (first by default)
        public tabsActive: Array<boolean> = []
        public isResRead: ICollapsedMap = {}

        private registryService: portal.RegistryService
        private methodsService: portal.MethodsService
        private userService: portal.UserService
        private state: ng.ui.IStateService
        private user: User
        private myScope: ng.IScope
        private growl: any

        constructor(registryService: portal.RegistryService, methodsService: portal.MethodsService,
            userService: portal.UserService, $state: ng.ui.IStateService, growl: any) {
            this.registryService = registryService
            this.methodsService = methodsService
            this.userService = userService
            this.state = $state
            this.growl = growl
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
            // @FIXME this slows down loading
            this.myScope.$watch('$includeContentLoaded', (e) => {          
                //console.log("UserDataDir loaded")     
                // collapsing all selections on init
                if(this.user.selections) {
                    this.user.selections.forEach((e) => { this.isCollapsed[e.id] = true })
                }
                // collapsing all votables on init
                if(this.user.voTables) {
                    this.user.voTables.forEach((e) => { this.isCollapsed[e.id] = true })
                }
                // collapsing all results on init / all are read by default
                if(this.user.results) {
                    this.user.results.forEach((e) => { 
                        this.isCollapsed[e.id] = true 
                        this.isResRead[e.id] = true
                    })
                }
                // if there is a successful result not yet read (per repository)
                if(this.methodsService.showSuccess[this.repositoryId]) {
                    // the first is always the latest result
                    this.isCollapsed[this.user.results[0].id] = false
                    this.tabsActive = [false, false, true] // selections, votables, results
                    this.methodsService.showSuccess[this.repositoryId] = false
                // if there are results not yet read (all in userdata state)
                } else if(this.methodsService.unreadResults > 0 && 
                    this.state.current.name == 'app.portal.userdata'){
                    this.tabsActive = [false, false, true] // selections, votables, results
                    this.isCollapsed[this.user.results[0].id] = false
                    for(var i = 0; i < this.methodsService.unreadResults; i++){
                        this.isResRead[this.user.results[i].id] = false
                    }
                } else {
                    // init tabs
                    this.tabsActive = [true, false, false] // selections, votables, results
                    this.methodsService.showSuccess[this.repositoryId] = false
                    this.methodsService.showError[this.repositoryId] = false
                }
                // reset expanded selections  
                this.user.activeSelection = []
                // reset applyables
                this.applyableElements = []
                this.applyableModel = null   
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
            // @FIXME maybe not valid for all use cases
            this.myScope.$on('set-applyable-models', (e, m: string) => { 
                console.log(m)
                console.log('set-applyable-models')
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
                    if(rId != id) this.isCollapsed[rId] = true
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
                    if(rId != id) this.isCollapsed[rId] = true
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
                    if(rId != id) this.isCollapsed[rId] = true
                }
                // set result tab active
                this.tabsActive = this.tabsActive.map((t) => t = false)
                this.tabsActive[2] = true
            })
            
        }
        
        public isSelectable(type: string): boolean {
            if(angular.isDefined(this.selectables)) {
                // there is nothing to select in my data modal
                if(this.state.current.name == 'app.portal.userdata') {
                    return false
                // hack for SINP models
                } else if(type == 'SimulationModel' && 
                    this.repositoryId.indexOf('SINP') != -1 && 
                    this.user.activeSelection.length == 1) {
                    if(this.user.activeSelection[0].elem.resourceId.indexOf('Static') != -1)
                        return false
                    else
                        return true
                } else if(type == 'Granule') {
                    return true
                } else {
                    return this.selectables.indexOf(type) != -1
                } 
           }
        }
        
        public isSelSaved(id: string): boolean {
            if(this.user.selections.filter((s) => s.id == id).length == 1)
                return true
            else
                return false
        }
        
        public saveSelection(id: string) { 
            this.isCollapsed[id] = false
            // closing all other collapsibles
            for(var rId in this.isCollapsed) {
                if(rId != id) this.isCollapsed[rId] = true
            }
            this.userService.user.selections = this.user.activeSelection.concat(this.userService.user.selections)
            // refresh localStorage
            this.userService.localStorage.selections = this.userService.user.selections
            // set selections tab active
            this.tabsActive = this.tabsActive.map((t) => t = false)
            this.tabsActive[0] = true
            this.growl.success('Saved selection to user data')
        }
         
        public toggleSelectionDetails(id: string) {
            if(this.isCollapsed[id]) { // if it is closed 
                this.isCollapsed[id] = false
                // closing all other collapsibles
                for(var rId in this.isCollapsed) {
                    if(rId != id) this.isCollapsed[rId] = true
                }
                // get selection
                var selection = this.user.selections.filter((e) => e.id == id)[0]
                if(this.applyableElements.indexOf(selection.type) != -1) {
                    this.isSelApplyable = true  
                    // here we check SINP models/outputs
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
                // reset expanded selection
                this.user.activeSelection = []
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
                    if(rId != id) this.isCollapsed[rId] = true
                }
            } else {
                this.isCollapsed[id] = true
            }
            // check if the details need to be marked read
            if(id in this.isResRead){
                if(!this.isResRead[this.user.results[0].id]){
                    this.isResRead[this.user.results[0].id] = true
                    this.methodsService.unreadResults--
                }
                if(!this.isResRead[id]){
                    this.isResRead[id] = true
                    this.methodsService.unreadResults--
                }
            }
        }
          
        public deleteSelection(id: string) {
            // update global service/localStorage
            this.userService.user.selections = 
                this.userService.user.selections.filter((e) => e.id != id)
            this.userService.localStorage.selections = this.userService.user.selections 
            // safely clear currentSelection
            this.user.activeSelection = []
            // delete collapsed info
            delete this.isCollapsed[id]
            this.growl.info('Deleted selection from user data')
        }
        
        public deleteVOTable(vot: IUserData) {
            // update global service
            this.userService.user.voTables = 
                this.userService.user.voTables.filter((e) => e.id != vot.id)
            // delete file on server
            this.userService.deleteUserData(vot.url.split('/').reverse()[0])
            // delete collapsed info
            delete this.isCollapsed[vot.id]
            this.growl.info('Deleted VOTable from user data')
        }
 		
        public deleteResult(id: string) {
            // update global service/localStorage
            this.userService.user.results = 
                this.userService.user.results.filter((e) => e.id != id)
            this.userService.localStorage.results = this.userService.user.results
            // delete collapsed info
            delete this.isCollapsed[id]
            this.growl.info('Deleted result from user data')
        }
        
        // method for applying a selection to the current method
        public applySelection(resourceId: string) {
            this.myScope.$broadcast('apply-selection', resourceId)
        }
        
        // method for applying a votable url to the current method
        public applyVOTable(url: string) {
            this.myScope.$broadcast('apply-votable', url)
        }
        
        
    }

}