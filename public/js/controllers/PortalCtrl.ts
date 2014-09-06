/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private scope: portal.IPortalScope
        private window: ng.IWindowService
        private timeout: ng.ITimeoutService
        private state: ng.ui.IStateService
        private growl: any
        
        public ready: boolean = false
        public configService: portal.ConfigService
        public methodsService: portal.MethodsService
        public databasesTooltip: string = "Within the different IMPEx-databases, you can browse the trees of all<br/>"+
            "service providers for getting an overview of all available data and its metadata.<br/>"+
            "Suitable tree elements can be selected and will then be stored automatically in<br/>"+
            "the &ldquo;My Data&rdquo; dialog for their further usage in the &ldquo;Data Access&rdquo; area.<br/>"+
            "Please be aware that only the selections of the respective database will be<br/>"+
            "visible in the Databases and Data Access dialogs of the IMPEx Portal."
       public servicesTooltip: string = "Within this area you can browse through the methods of all IMPEx service providers.<br/>"+
            "Selections, which were chosen in &ldquo;Databases&rdquo; and stored in &ldquo;My Data&rdquo;, as well as<br/>"+
            "uploaded VOTables will be visible in the dialog, and can then easily be applied as far<br/>"+
            "as they are applicable for the respective methods.<br/>"+
            "Obtained results can be saved for further usage.<br/>"+
            "Please be aware that only the selections and results of the respective database will be<br/>"+
            "visible in the Databases and Data Access dialogs of the IMPEx Portal."
        public toolsTooltip: string = "The Tools area of the IMPEx Portal provides links to all tools for data analysis,<br/>"+
            "which are connected to the IMPEx services.<br/>"+
            "Via connecting to a SAMP Hub you can send selected service results to<br/>"+
            "the respective tools for further analysis.<br/>"+
            "A quick overview guide on all of these tools, as well as on the simulation<br/>"+
            "databases can be found in the Tool Docs.<br/>"
        public myDataTooltip: string = "&ldquo;My Data&rdquo; is the reservoir for all stored services and results,<br/>"+
            "which can easily be managed in this area.<br/>"+
            "Moreover customized VOTables can be uploaded via<br/>"+
            "&ldquo;My Data&rdquo; for further usage in the IMPEx Portal.<br/>"+
            "Please be aware that VOTable files are saved for only 24 hours.<br/>"+
            "Results and selections on the other hand are stored on the<br/>"+
            "client-side and will be available with no elapse time."
        public filterTooltip: string = "This function can be used to filter IMPEx databases and services via<br/>"+
            "customized criteria.<br/>"+
            "Those who do not fit the criteria will be deactivated."
      
        static $inject: Array<string> = ['$scope', '$window', '$timeout', 'configService', 
            'methodsService', '$state', 'growl']

        constructor($scope: IPortalScope, $window: ng.IWindowService, $timeout: ng.ITimeoutService, configService: portal.ConfigService, 
            methodsService: portal.MethodsService, $state: ng.ui.IStateService, growl: any) { 
            this.scope = $scope
            this.scope.vm = this
            this.window = $window
            this.timeout = $timeout
            this.configService = configService
            this.methodsService = methodsService
            this.state = $state
            this.growl = growl
            
            this.timeout(() => { 
                  this.ready = true 
                  this.growl.warning('Configuration loaded, waiting for isAlive...')
            })
            
            //console.log(this.configService.filterRegions)
            
            
            this.scope.$on('service-loading', (e, id: string) => { 
                console.log('loading at '+id)
            })
            
            this.scope.$on('service-success', (e, id: string) => {
                console.log('success at '+id)
            })
            
            this.scope.$on('service-error', (e, id: string) => { 
                console.log('error at '+id)
            })
        }
        
        public notImplemented() {
            this.window.alert("This functionality is not yet implemented.")
        }


    }
}