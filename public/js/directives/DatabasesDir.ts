/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IDatabasesDirScope extends ng.IScope {
       dbdirvm: DatabasesDir
    }

    export class DatabasesDir {

    	public injection(): any[] {
            return [
                '$timeout',
                'configService',
                ($timeout: ng.ITimeoutService, configService: portal.ConfigService) => 
                    { return new DatabasesDir($timeout, configService); }
            ]
        }

    	public link: ($scope: portal.IDatabasesDirScope, element: JQuery, attributes: ng.IAttributes) => any
    	public templateUrl: string
    	public restrict: string

        private timeout: ng.ITimeoutService
        private configService: portal.ConfigService
        private config: IConfig
        private myScope: ng.IScope

    	constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService) {
            this.timeout = $timeout
            this.configService = configService
            this.config = this.configService.config
	        this.templateUrl = '/public/partials/templates/databases.html'
	        this.restrict = 'E'
	        this.link = ($scope: portal.IDatabasesDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
            
	        
    	}

    	linkFn($scope: portal.IDatabasesDirScope, element: JQuery, attributes: ng.IAttributes): any {
            $scope.dbdirvm = this
            this.myScope = $scope
        }
    }

}