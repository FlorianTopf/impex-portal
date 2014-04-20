/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IPortalScope extends ng.IScope {
        vm: PortalCtrl
    }

    export class PortalCtrl {
        private config: IConfig
        private configService: portal.ConfigService
        private registryService: portal.RegistryService

        private http: ng.IHttpService
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private interval: ng.IIntervalService
        private window: ng.IWindowService
        private scope: portal.IPortalScope
        private configPromise: ng.IPromise<any>
        // just for testing
        private registryPromise1: ng.IPromise<any>
        private registryPromise2: ng.IPromise<any>
        
        private configAble: boolean = true

        private ready: boolean = false
      
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout',
            '$interval','$window', 'configService', 'registryService']

        private num: number = 1
        private treedata
        private selected

        // dependencies are injected via AngularJS $injector
        // controller's name is registered in App.ts and invoked from ng-controller attribute in index.html
        constructor($scope: IPortalScope, $http: ng.IHttpService, $location: ng.ILocationService, 
            $timeout: ng.ITimeoutService, $interval: ng.IIntervalService,
            $window: ng.IWindowService, configService: portal.ConfigService, registryService: portal.RegistryService) { 
            this.scope = $scope
            this.scope.vm = this
            this.http = $http
            this.location = $location
            this.configService = configService
            this.config = this.configService.config
            this.registryService = registryService
            this.timeout = $timeout
            this.interval = $interval
            this.window = $window
                
            this.treedata = this.createSubTree(7)
            
            if(this.config == null) {
                $location.path('/config')
            } else {
                 this.timeout(() => { 
                    this.ready = true 
                    // PLAYING AROUND WITH ANGULAR RESOURCES
                    this.doSomething()
                 })
            }
        }

        public doSomething() {
            // don know if this is the optimal way to do it 
            this.registryPromise1 = this.registryService.getRepository().get({ fmt: 'json' }).$promise
            this.registryPromise1.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => { console.log("repository="+res.repository.resourceId) })
            })   
            // don know if this is the optimal way to do it 
            this.registryPromise2 = this.registryService.getSimulationModel().get({ fmt: 'json' }).$promise
            this.registryPromise2.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => { console.log("model="+res.simulationModel.resourceId) })
            })  
        }
        
        public getNum() {
             return this.num++;
        }

        public createSubTree(level) {
            if (level > 0)
                return [
                    { "label" : "Node " + this.getNum(), "id" : "id", "children": this.createSubTree(level-1) },
                    { "label" : "Node " + this.getNum(), "id" : "id", "children": this.createSubTree(level-1) },
                    { "label" : "Node " + this.getNum(), "id" : "id", "children": this.createSubTree(level-1) },
                    { "label" : "Node " + this.getNum(), "id" : "id", "children": this.createSubTree(level-1) }
                ];
            else
                return [];
                
        }
    
        public showSelected(sel) {
            this.selected = sel.label;
        }

        public addRoot() {
            this.treedata.push({ "label" : "New Node " + this.getNum(), "id" : "id", "children": [] });
        }
        
        public addChild() {
            this.treedata[0].children.push({ "label" : "New Node " + this.getNum(), "id" : "id", "children": [] });
        }



    }
}
