/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }
        
    // needed for external SAMP lib
    declare var samp: any
    declare var isSampRegistered: boolean
    
    export class PortalCtrl {
        private scope: portal.IPortalScope
        // we must use any here (to access global vars)
        private window: any
        private timeout: ng.ITimeoutService
        private state: ng.ui.IStateService
        private growl: any
        
        public ready: boolean = false
        public configService: portal.ConfigService
        public methodsService: portal.MethodsService
        public registryService: portal.RegistryService
        public sampService: portal.SampService
        public databasesTooltip: string = 'Within the different IMPEx-databases, you can browse the trees of all<br/>'+
            'service providers for getting an overview of all available data and its metadata.<br/>'+
            'Suitable tree elements can be selected and will then be stored automatically in<br/>'+
            'the &ldquo;My Data&rdquo; dialog for their further usage in the &ldquo;Data Access&rdquo; area.<br/>'+
            'Please be aware that only the selections of the respective database will be<br/>'+
            'visible in the Databases and Data Access dialogs of the IMPEx Portal.'
       public servicesTooltip: string = 'Within this area you can browse through the methods of all IMPEx service providers.<br/>'+
            'Selections, which were chosen in &ldquo;Databases&rdquo; and stored in &ldquo;My Data&rdquo;, as well as<br/>'+
            'uploaded VOTables will be visible in the dialog, and can then easily be applied as far<br/>'+
            'as they are applicable for the respective methods.<br/>'+
            'Obtained results can be saved for further usage.<br/>'+
            'Please be aware that only the selections and results of the respective database will be<br/>'+
            'visible in the Databases and Data Access dialogs of the IMPEx Portal.'
        public toolsTooltip: string = 'The Tools area of the IMPEx Portal provides links to all tools for data analysis,<br/>'+
            'which are connected to the IMPEx services.<br/>'+
            'Via connecting to a SAMP Hub you can send selected service results to<br/>'+
            'the respective tools for further analysis.<br/>'+
            'A quick overview guide on all of these tools, as well as on the simulation<br/>'+
            'databases can be found in the Tool Docs.<br/>'
        public myDataTooltip: string = '&ldquo;My Data&rdquo; is the reservoir for all stored services and results,<br/>'+
            'which can easily be managed in this area.<br/>'+
            'Moreover customized VOTables can be uploaded via<br/>'+
            '&ldquo;My Data&rdquo; for further usage in the IMPEx Portal.<br/>'+
            'Please be aware that VOTable files are saved for only 24 hours.<br/>'+
            'Results and selections on the other hand are stored on the<br/>'+
            'client-side and will be available with no elapse time.'
        public filterTooltip: string = 'This function can be used to filter IMPEx databases and services via<br/>'+
            'customized criteria.<br/>'+
            'Those who do not fit the criteria will be deactivated.'
        public isFilterCollapsed: boolean = true
        public isFilterLoading: boolean = false
        public isFilterSelected: boolean = false
        public isSampCollapsed: boolean = true
        // active path from database
        public activeDatabase: string = null
        // active path from service
        public activeService: string = null

      
        static $inject: Array<string> = ['$scope', '$window', '$timeout', 'configService', 
            'methodsService', 'registryService', 'sampService', '$state', 'growl']

        constructor($scope: IPortalScope, $window: any, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            methodsService: portal.MethodsService, registryService: portal.RegistryService, 
            sampService: portal.SampService, $state: ng.ui.IStateService, growl: any) { 
            this.scope = $scope
            this.scope.vm = this
            this.window = $window
            this.timeout = $timeout
            this.configService = configService
            this.methodsService = methodsService
            this.registryService = registryService
            this.sampService = sampService
            this.state = $state
            this.growl = growl
            
            this.timeout(() => { 
                  this.ready = true 
                  this.growl.warning('Configuration loaded, waiting for isAlive...')
            })
            
            this.configService.filterRegions.forEach((r) => {
                this.registryService.selectedFilter[r] = false
            })
            
            // monitoring all current clients
            this.sampService.clientTracker.onchange = (id, type, data) => {
                this.sampService.clients = {}
                var ids = this.sampService.clientTracker.connection ? this.sampService.clientTracker.ids : []
                for(var id in ids){
                    this.sampService.clients[id] = {
                        'metas': this.sampService.clientTracker.metas[id],
                        'subs': this.sampService.clientTracker.subs[id]
                    }
                    //console.log(JSON.stringify(this.sampService.clients[id].subs))
                }
            }
            
            // unregister from SAMP when navigating away
            var onBeforeUnloadHandler = (e) => {
                this.sampService.connector.unregister()
                // accessing the global var
                this.window.isSampRegistered = false 
                this.sampService.clients = {}
            }
            if (this.window.addEventListener) {
                this.window.addEventListener('beforeunload', onBeforeUnloadHandler)
            } else {
                this.window.onbeforeunload = onBeforeUnloadHandler
            }
            
            // just for testing
            //this.activeDatabase = 'FMI-HYBRID'
            //this.activeService = 'SINP'
            
            this.scope.$on('service-success', (e, id: string) => {
                console.log('service success at '+id)
                this.activeDatabase = null
                // add symbols
                this.activeService = this.configService.getDatabase(id).name
            })
 
            this.scope.$on('database-success', (e, id: string) => {
                console.log('database success at '+id)
                this.activeService = null
                // add symbols
                this.activeDatabase = this.configService.getDatabase(id).name
            })
            
        }
        
        //public notImplemented() {
        //    this.window.alert('This functionality is not yet implemented.')
        //}
        
        public toggleFilter() {
            this.isFilterCollapsed = !this.isFilterCollapsed
            this.scope.$broadcast('draw-paths')
        }
        
        public selectFilter(region: string) {
            this.registryService.selectedFilter[region] = 
                !this.registryService.selectedFilter[region]
            this.isFilterSelected = false
            for(var region in this.registryService.selectedFilter) {
                if(this.registryService.selectedFilter[region]){
                    this.isFilterSelected = true
                    break
                }
            } 
        }
        
        public resetFilter() {
            for(var region in this.registryService.selectedFilter) {
                this.registryService.selectedFilter[region] = false
            }
            for(var id in this.configService.filterMap) {
                this.configService.filterMap[id] = true
            }
            this.registryService.isFilterSet = false
            this.isFilterSelected = false
            //console.log(JSON.stringify(this.configService.filterMap))
        }
        
        public requestFilter() {
            this.isFilterCollapsed = true
            this.isFilterLoading = true
            this.registryService.isFilterSet = false
            var counter = 0
            var tempMap: IBooleanMap = {}
            for(var region in this.registryService.selectedFilter) {
                if(this.registryService.selectedFilter[region]) {
                    counter++
                    this.configService.filterRegion(region)
                    .success((data: Array<string>, status: any) => { 
                        //console.log(data)
                        counter--            
                        data.forEach((e) => { tempMap[e] = true })
                        if(counter == 0) {
                            // refreshing the real filterMap
                            for(var id in this.configService.filterMap) {
                                if(id in tempMap)
                                    this.configService.filterMap[id] = true
                                else
                                    this.configService.filterMap[id] = false
                            }
                            this.isFilterLoading = false
                            this.registryService.isFilterSet = true
                            //console.log(JSON.stringify(this.configService.filterMap))
                        }
                    })
                    // @TODO what to do in an error case?
                    .error((data: any, status: any) => {
                        this.isFilterLoading = false
                        this.registryService.isFilterSet = false
                    })
                }
            }
        }
        
        public toggleSamp() {
            this.isSampCollapsed = !this.isSampCollapsed
            if(this.isFilterCollapsed)
                this.scope.$broadcast('draw-paths')
        }
        
        // @FIXME delay because of isSampRegistered is a global var
        public registerSamp() {
            this.growl.warning('Contacting SAMP hub, please wait...')
            //this.sampConnector.register()
            var send = (connection) => {
                // check if this is working
                connection.notifyAll([new samp.Message("samp.app.ping", {})])
                // accessing the global var
                isSampRegistered = true
            }
            var error = (e) => {
                alert("No Hub available. Please start an external Hub before registering.")
                // accessing the global var
                isSampRegistered = false
            }
            this.sampService.connector.runWithConnection(send, error)
            
        }
        
        public unregisterSamp() {
            this.sampService.connector.unregister()
            this.window.isSampRegistered = false
            this.sampService.clients = {}
        }
        
        // just for testing basic sending
        public sendToSamp() {
            // URL of table to send.
            var tableUrl = "http://impex-fp7.fmi.fi/ws/data/VOT_4kuZOr8ihD.vot"
            // broadcasts a table given a hub connection
            var send = (connection) => {
                var msg = new samp.Message("table.load.votable", { "url": tableUrl })
                connection.notifyAll([msg])
            }
            // In any error case call this
            var error = (e) => {
                console.log("Error "+e)
            }
            this.sampService.connector.runWithConnection(send, error)
        }
        
        public resetPath() {
            this.activeDatabase = null
            this.activeService = null
            this.scope.$broadcast('clear-paths')
        }
        

    }
}