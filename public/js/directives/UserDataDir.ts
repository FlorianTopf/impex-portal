/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IUserDataDirScope extends ng.IScope {
       userdirvm: UserDataDir
    }
    
    // needed for external SAMP lib
    declare var samp: any

    export class UserDataDir implements ng.IDirective {

        public injection(): any[] {
            return [
                'registryService',
                'methodsService',
                'userService',
                'sampService',
                '$window',
                '$state',
                'growl',
                (registryService: portal.RegistryService, methodsService: portal.MethodsService,
                userService: portal.UserService, sampService: portal.SampService, $window: ng.IWindowService, 
                    $state: ng.ui.IStateService, growl: any) => 
                    { return new UserDataDir(registryService, methodsService, userService, 
                        sampService, $window, $state, growl); }
            ]
        }

        public link: ($scope: portal.IUserDataDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        public repositoryId: string = null
        public isCollapsed: IBooleanMap = {}
        public isResRead: IBooleanMap = {}
        // active tabs (first by default)
        public tabsActive: Array<boolean> = [true, false, false] // selections, votables, results
        public selectables: Array<string> = []
        // currently applyable elements (actual method)
        public applyableElements: Array<string> = []
        // for SINP models/outputs
        public applyableModel: string = null
        // flag if selection is applyable
        public isSelApplyable: boolean = false
        // flag if votable is applyable
        public isVOTApplyable: boolean = false
        public isSampAble: boolean = false
        public isLogCollapsed: boolean = true

        private registryService: portal.RegistryService
        private methodsService: portal.MethodsService
        private userService: portal.UserService
        private sampService: portal.SampService
        private window: ng.IWindowService
        private state: ng.ui.IStateService
        private user: User
        private myScope: ng.IScope
        private growl: any

        constructor(registryService: portal.RegistryService, methodsService: portal.MethodsService,
            userService: portal.UserService, sampService: portal.SampService, $window: ng.IWindowService, 
            $state: ng.ui.IStateService, growl: any) {
            this.registryService = registryService
            this.methodsService = methodsService
            this.userService = userService
            this.sampService = sampService
            this.window = $window
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
                if(id) {
                    this.selectables = this.registryService.selectables[id]
                } else {
                    this.selectables = []
                }
                // must be undefined by default so that filter works
                this.repositoryId = id
                // reset applyables
                this.applyableElements = []
                this.applyableModel = null   
                this.isSelApplyable = false
                this.isVOTApplyable = false
            })
            
            // watch event when all content is loaded into the dir
            this.myScope.$watch('$includeContentLoaded', (e) => {          
                // check samp clients
                if(this.sampService.clients) {
                    for(var id in this.sampService.clients){
                        var subs = this.sampService.clients[id].subs
                        if(subs.hasOwnProperty('table.load.votable'))
                            this.isSampAble = true
                    }
                }  
                 
                // collapsing all selections on init
                this.user.selections.forEach((e) => { this.isCollapsed[e.id] = true })
                // collapsing all votables on init
                this.user.voTables.forEach((e) => { this.isCollapsed[e.id] = true })
                // collapsing all results on init / all are read by default
                this.user.results.forEach((e) => { 
                        this.isCollapsed[e.id] = true 
                        this.isResRead[e.id] = true
                })
                
                // if there is a successful result not yet read (per repository)
                if(this.methodsService.showSuccess[this.repositoryId] && 
                    this.state.current.name == 'app.portal.methods') {
                    this.tabsActive = [false, false, true] // selections, votables, results
                    // takes the latest from a specific repo
                    var latest = this.user.results.filter((r) => r.repositoryId == this.repositoryId)[0]
                    // the first is always the latest result
                    this.setCollapsedMap(latest.id)
                    this.isResRead[latest.id] = true
                    this.methodsService.showSuccess[this.repositoryId] = false
                    this.methodsService.unreadResults--
                // if there was an error in the last request => clear (per repository)
                } else if(this.methodsService.showError[this.repositoryId] && 
                    this.state.current.name == 'app.portal.methods') {
                    this.methodsService.showError[this.repositoryId] = false
                // if there are more results not yet read (all in userdata state)
                } else if(this.methodsService.unreadResults > 0 && 
                    this.state.current.name == 'app.portal.userdata') {
                    this.tabsActive = [false, false, true] // selections, votables, results
                    this.setCollapsedMap(this.user.results[0].id)
                    this.isResRead[this.user.results[0].id] = true
                    for(var i = 1; i < this.methodsService.unreadResults; i++){
                        this.isResRead[this.user.results[i].id] = false
                    }
                    this.methodsService.unreadResults--
                // set active selection if we enter the right registry
                // otherwise clear active selections
                } else if(this.state.current.name == 'app.portal.registry')  {
                    this.user.activeSelection.forEach((s) => {
                        if(s.repositoryId == this.repositoryId)
                             this.setCollapsedMap(s.id)
                        else
                             this.user.activeSelection = []
                    })
                // set active selection if we enter the right methods
                // otherwise clear active selections
                } else if(this.state.current.name == 'app.portal.methods') {
                    this.user.activeSelection.forEach((s) => {
                        if(s.repositoryId == this.repositoryId && this.isSelectable(s.type))
                             this.setCollapsedMap(s.id)
                        else 
                             this.user.activeSelection = []
                    })
                // default behaviour: attention this is only applyable when there 
                // is one active selection
                } else {
                    this.user.activeSelection.forEach((s) => {
                        if(this.isSelSaved(s.id))
                            this.setCollapsedMap(s.id)
                        else
                            this.isCollapsed[s.id] = true
                    })
                }
                
            })
            
            // comes from MethodsDir
            this.myScope.$on('set-applyable-elements', (e, elements: string) => {
               //console.log(elements)
               //console.log('set-applyable-elements')
               this.applyableElements = elements.split(',').map((e) => e.trim())
               this.isSelApplyable = false
               this.user.activeSelection.forEach((s) => {
                    this.applyableElements.forEach((e) => {
                        if(s.repositoryId == this.repositoryId && s.type == e)
                            this.isSelApplyable = true  
                    })
               })
            })
            
            // comes from MethodsDir
            this.myScope.$on('set-applyable-votable', (e, b: boolean) => { 
                this.isVOTApplyable = b
            })
            
            // comes from MethodsDir => hack for SINP models/output
            this.myScope.$on('set-applyable-model', (e, model: string) => { 
                //console.log(m)
                //console.log('set-applyable-model')
                this.applyableModel = model
                this.user.activeSelection.forEach((s) => {
                    // we just check the onfly numerical output elements too
                    var output = this.applyableModel.replace('SimulationModel', 'NumericalOutput')
                    if(this.applyableModel == s.elem.resourceId)
                        this.isSelApplyable = true
                    else if(s.elem.resourceId.indexOf(output) != -1)
                        this.isSelApplyable = true
                    else if(this.applyableModel == 'Static' && 
                        s.elem.resourceId.indexOf('OnFly') == -1)
                        this.isSelApplyable = true
                    else
                        this.isSelApplyable = false
                })    
            })
            
            // comes from RegistryDir
            this.myScope.$on('update-selections', (e, id: string) => {
                this.setCollapsedMap(id)
                // set selections tab active
                this.tabsActive = [true, false, false] // selections, votables, results
            })
            
            // comes from UserDataCtrl
            this.myScope.$on('update-votables', (e, id: string) => {
                // reset expanded selection
                this.user.activeSelection = []
                this.setCollapsedMap(id)
                // set votable tab active
                this.tabsActive = [false, true, false] // selections, votables, results
            })
            
            // comes from MethodsCtrl
            this.myScope.$on('update-results', (e, id: string) => {
                // reset expanded selection
                this.user.activeSelection = []
                this.isResRead[id] = true
                this.setCollapsedMap(id)
                // set result tab active
                this.tabsActive = [false, false, true] // selections, votables, results
            })
            
        }
        
        private setCollapsedMap(id: string) {
            this.isCollapsed[id] = false
            // closing all other collapsibles
            for(var rId in this.isCollapsed) {
                if(rId != id) this.isCollapsed[rId] = true
            }
        }
        
        public isSelectable(type: string): boolean {
            // there is nothing to select when no db is provided
            if(this.state.current.name == 'app.portal.userdata') {
                return false
            } else if(this.repositoryId) {
                // hack for SINP models
                if(type == 'SimulationModel' && 
                    this.repositoryId.indexOf('SINP') != -1) {
                    var selectable = false
                    this.user.activeSelection.forEach((s) => { 
                        if(s.elem.resourceId.indexOf('OnFly') != -1)
                            selectable = true
                    })
                    return selectable
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
        
        // @TODO prevent saveSelection if there is already the same resourceid saved
        public saveSelection(id: string) { 
            this.setCollapsedMap(id)
            this.user.selections = this.user.activeSelection.concat(this.user.selections)
            // refresh localStorage
            this.userService.localStorage.selections = this.user.selections
            // set selections tab active
            this.tabsActive = [true, false, false] // selections, votables, results
            this.growl.success('Saved selection to user data')
            this.registryService.notify('success', this.repositoryId)
        }
         
        public toggleSelectionDetails(id: string) {
            if(this.isCollapsed[id]) { // if it is closed 
                this.setCollapsedMap(id)
                // get selection
                var sel = this.user.selections.filter((e) => e.id == id)[0]
                if(this.applyableElements.indexOf(sel.type) != -1) {
                    this.isSelApplyable = true  
                    // hack for SINP models/outputs
                    if(this.applyableModel) {
                        var output = this.applyableModel.replace('SimulationModel', 'NumericalOutput')
                        if(this.applyableModel == sel.elem.resourceId)
                            this.isSelApplyable = true
                        else if(sel.elem.resourceId.indexOf(output) != -1)
                            this.isSelApplyable = true
                        else if(this.applyableModel == 'Static' && 
                            sel.elem.resourceId.indexOf('OnFly') == -1)
                            this.isSelApplyable = true
                        else
                            this.isSelApplyable = false
                    }  
                } else {
                    this.isSelApplyable = false
                }
                this.user.activeSelection = [sel] 
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
                this.setCollapsedMap(id)
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
            this.user.selections = 
                this.user.selections.filter((e) => e.id != id)
            this.userService.localStorage.selections = this.user.selections 
            // safely clear currentSelection
            this.user.activeSelection = []
            // delete collapsed info
            delete this.isCollapsed[id]
            this.growl.info('Deleted selection from user data')
        }
        
        public deleteVOTable(vot: IUserData) {
            // update global service
            this.user.voTables = 
                this.user.voTables.filter((e) => e.id != vot.id)
            // delete file on server
            this.userService.deleteUserData(vot.url.split('/').reverse()[0])
            // delete collapsed info
            delete this.isCollapsed[vot.id]
            this.growl.info('Deleted votable from user data')
        }
 		
        public deleteResult(id: string) {
            // update global service/localStorage
            this.user.results = 
                this.user.results.filter((e) => e.id != id)
            this.userService.localStorage.results = this.user.results
            // delete collapsed info
            delete this.isCollapsed[id]
            this.growl.info('Deleted result from user data')
        }
        
        // method for applying a selection to the current method
        public applySelection(resourceId: string) {
            var keys = null
            this.user.activeSelection.forEach((s) => {
                if(s.repositoryId == this.repositoryId && s.elem.resourceId == resourceId) {
                    if(s.type == 'NumericalOutput') {
                        var output = <NumericalOutput>s.elem
                        keys = output.parameter.map((p) => p.parameterKey)
                    }
                    if(s.type == 'NumericalData') {
                        var data = <NumericalData>s.elem
                        keys = data.parameter.map((p) => p.parameterKey)
                    }
                }
            })
            this.myScope.$broadcast('apply-selection', resourceId, keys)
        }
        
        // method for applying a votable url to the current method
        public applyVOTable(msg: any) {
            //console.log("MSG: "+JSON.stringify(msg))
            // this is a little hack for AMDA results (they can be applied too)
            if(msg.hasOwnProperty('dataFileURLs'))
                this.myScope.$broadcast('apply-votable', msg['dataFileURLs'])
            else if(angular.isString(msg))
                this.myScope.$broadcast('apply-votable', msg)
        }
        
        public clearData(type: string) {
            if(this.window.confirm('Do you want to delete all '+type+'?')) {
                if(type == 'selections') {
                    // reset expanded selection
                    this.user.activeSelection = []
                    // delete all
                    if(this.state.current.name == 'app.portal.userdata') {
                        this.user.selections.forEach((sel) => {
                            delete this.isCollapsed[sel.id]
                        })
                        this.user.selections = []
                        this.userService.localStorage.selections = null
                    } else {
                        var removedSel = this.user.selections.filter(
                            (s) => s.repositoryId == this.repositoryId)
                        removedSel.forEach((sel) => {
                            delete this.isCollapsed[sel.id]
                        })
                        this.user.selections = this.user.selections.filter(
                            (s) => s.repositoryId != this.repositoryId)
                        this.userService.localStorage.selections = this.user.selections
                    }
                } else if(type == 'votables') {
                    this.user.voTables.forEach((vot) => {
                        this.userService.deleteUserData(vot.url.split('/').reverse()[0])
                        delete this.isCollapsed[vot.id]
                    })
                    this.user.voTables = []
                    this.tabsActive = [true, false, false] // selections, votables, results
                } else if(type == 'results') {
                    // delete all
                    if(this.state.current.name == 'app.portal.userdata') {
                        this.user.results.forEach((res) => {
                            delete this.isCollapsed[res.id]
                        })
                        this.user.results = []
                        this.userService.localStorage.results = null
                    } else {
                        var removedRes = this.user.results.filter(
                            (s) => s.repositoryId == this.repositoryId)
                        removedRes.forEach((res) => {
                            delete this.isCollapsed[res.id]
                        })
                        this.user.results = this.user.results.filter(
                            (s) => s.repositoryId != this.repositoryId)
                        this.userService.localStorage.results = this.user.results
                    }
                    this.tabsActive = [true, false, false] // selections, votables, results
                }
                if(this.state.current.name == 'app.portal.userdata')  {
                    this.growl.info('Deleted all '+type+' from user data')
                } else
                    this.growl.info('Deleted all '+type+' of '+this.repositoryId+' from user data')
             }
        }
        
        // @TODO we must check if the Url is still valid (empty or not found)
        public sendToSamp(tableUrl: any, clientId: string) {
            //console.log('sending '+JSON.stringify(tableUrl)+' '+id)
            // broadcasts a table given a hub connection
            var send = (connection) => {
                if(tableUrl.hasOwnProperty('dataFileURLs')) {
                    tableUrl.dataFileURLs.forEach((e) => {
                       var msg = new samp.Message("table.load.votable", { "url": e })
                       connection.notify([clientId, msg])                
                    })
                } else {
                    var msg = new samp.Message("table.load.votable", { "url": tableUrl })
                    connection.notify([clientId, msg])
                }
            }
            // in any error case call this
            var error = (e) => {
                 this.growl.error("Error with SAMP "+e)
            }
            this.sampService.connector.runWithConnection(send, error)
        }
        
        
    }
    
}