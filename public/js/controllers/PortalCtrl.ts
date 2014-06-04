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
        private registryPromiseRepo: ng.IPromise<any>
        private registryPromiseModel: ng.IPromise<any>
        private registryPromiseRun: ng.IPromise<any>
        private registryPromiseOutput: ng.IPromise<any>
        private registryPromisegranule: ng.IPromise<any>
        
        private configAble: boolean = true
        private ready: boolean = false
        private loading: boolean = false
        private transFinished: boolean = true
        private oneAtATime: boolean = true
      
        static $inject: Array<string> = ['$scope', '$http', '$location', '$timeout',
            '$interval','$window', 'configService', 'registryService']

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
            
            if(this.config == null) {
                $location.path('/config')
            } else {
                 this.timeout(() => { 
                    this.ready = true 
                 })
            }
                        
        }
        
        // @TODO cache the current focus of resources,
        // include custom events! 
        
        public getRepository(id: string) {
            // @FIXME improve this
            this.registryService.repositories = []
            this.registryService.simulationModels = []
            this.registryService.simulationRuns = []
            this.loading = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            // @FIXME dont know if this is the optimal way to do it 
            this.registryPromiseRepo = this.registryService.getRepository().get(
                { fmt: 'json' , id: id }).$promise
            this.registryPromiseRepo.then((res) => {
                var result = <ISpase>res.spase
                // @TODO here we should save the thing in a map
                result.resources.forEach((res) => { 
                    //console.log("repository="+res.repository.resourceId) 
                    this.registryService.repositories.push(res.repository)
                })
                this.loading = false
            })
            
        }
        
        // resource id must be transformed from repository id! 
        //=> check if there is more than one repository
        public getSimulationModel(id: string) {
            // @FIXME improve this
            this.registryService.simulationModels = []
            this.registryService.simulationRuns = []
            this.loading = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            // @FIXME dont know if this is the optimal way to do it 
            this.registryPromiseModel = this.registryService.getSimulationModel().get(
                { fmt: 'json', id: id }).$promise
            this.registryPromiseModel.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => { 
                    //console.log("model="+res.simulationModel.resourceId) 
                    this.registryService.simulationModels.push(res.simulationModel)
                })
                this.loading = false
            }) 
            
        }
        
        public getSimulationRun(id: string) {
            // @FIXME improve this
            this.registryService.simulationRuns = []
            this.loading = true
            this.transFinished = false
            // aligned with standard transition time of accordion
            this.timeout(() => { this.transFinished = true }, 200)
            // @FIXME dont know if this is the optimal way to do it 
            this.registryPromiseRun = this.registryService.getSimulationRun().get(
                { fmt: 'json', id: id }).$promise
            this.registryPromiseRun.then((res) => {
                var result = <ISpase>res.spase
                result.resources.forEach((res) => { 
                    //if(res.simulationRun.simulationDomain.boundaryConditions)
                        //console.log("run="+res.simulationRun.resourceId+" "+
                    //        res.simulationRun.simulationDomain.boundaryConditions.fieldBoundary.frontWall) 
                    this.registryService.simulationRuns.push(res.simulationRun)
                })
                this.loading = false
            })
                  
        }
        
        // testing of angular resources
        public doSomething() {
            /* 
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
            }) */
        }

    }
}
