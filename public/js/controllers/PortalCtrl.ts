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
        private registryPromiseRepo: ng.IPromise<any>
        private registryPromiseModel: ng.IPromise<any>
        private registryPromiseRun: ng.IPromise<any>
        private registryPromiseOutput: ng.IPromise<any>
        
        private configAble: boolean = true

        private ready: boolean = false
      
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout',
            '$interval','$window', 'configService', 'registryService']

        private num: number = 1
        private treedata: Array<any>
        private selected: string

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
            // dont know if this is the optimal way to do it 
            this.registryPromiseRepo = this.registryService.getRepository().get(
                { fmt: 'json' , id: 'impex://LATMOS' }).$promise
            this.registryPromiseRepo.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => { console.log("repository="+res.repository.resourceId) })
            })   
            this.registryPromiseModel = this.registryService.getSimulationModel().get(
                { fmt: 'json', id: 'impex://SINP/SimulationModel/Earth' }).$promise
            this.registryPromiseModel.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => { console.log("model="+res.simulationModel.resourceId) })
            })  
            this.registryPromiseRun = this.registryService.getSimulationRun().get(
                { fmt: 'json', id: 'impex://FMI/HWA/GUMICS/earth' }).$promise
            this.registryPromiseRun.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => { 
                    if(res.simulationRun.simulationDomain.boundaryConditions)
                        console.log("run="+res.simulationRun.resourceId+" "+
                            res.simulationRun.simulationDomain.boundaryConditions.fieldBoundary.frontWall) })
            })  
            this.registryPromiseOutput = this.registryService.getNumericalOutput().get(
                { fmt: 'json', id: 'impex://LATMOS/Hybrid/Gany_24_10_13' }).$promise
            this.registryPromiseOutput.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => {
                    console.log("outputId="+res.numericalOutput.resourceId)
                    
                    if(res.numericalOutput.spatialDescription)
                        console.log("outputS="+
                            res.numericalOutput.spatialDescription.coordinateSystem.coordinateRepresentation)
                     
                    if(res.numericalOutput.temporalDescription)
                        console.log("outputT="+res.numericalOutput.temporalDescription.timespan.startDate)
                })
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
