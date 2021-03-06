/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IDatabasesDirScope extends ng.IScope {
       dbdirvm: DatabasesDir
    }

    export class DatabasesDir implements ng.IDirective {

    	public injection(): any[] {
            return [
                'configService',
                (configService: portal.ConfigService) => 
                    { return new DatabasesDir(configService) }
            ]
        }

    	public link: ($scope: portal.IDatabasesDirScope, element: JQuery, attributes: ng.IAttributes) => any
    	public templateUrl: string
    	public restrict: string
        public config: IConfig
        public statusMap: IStatusMap

        private myScope: portal.IDatabasesDirScope
        private configService: portal.ConfigService

    	constructor(configService: portal.ConfigService) {
            this.configService = configService
	        this.templateUrl = '/public/partials/templates/databasesDir.html'
	        this.restrict = 'E'
	        this.link = ($scope: portal.IDatabasesDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
    	}

    	linkFn($scope: portal.IDatabasesDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.dbdirvm = this
            this.myScope = $scope
            this.config = this.configService.config
            
            this.configService.getStatus().success((data: IStatusMap, status: any) => {
                this.statusMap = data
                for (var id in this.statusMap){
                    this.statusMap[id].lastUpdate = new Date(this.statusMap[id].lastUpdate).toString()
                    if(this.statusMap[id].lastError) 
                       this.statusMap[id].lastUpdate = new Date(this.statusMap[id].lastError).toString()
                }
            }).error((data: any, status: any) => {
                this.statusMap = {}
            })
        }
    }

}