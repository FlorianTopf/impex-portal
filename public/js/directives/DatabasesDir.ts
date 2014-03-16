/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IDatabasesDirScope extends ng.IScope {
       databasesvm: DatabasesDir
    }

    export class DatabasesDir {

    	public injection(): any[] {
            return [
                '$timeout',
                'configService',
                ($timeout: ng.ITimeoutService, configService: portal.ConfigService) => { return new DatabasesDir($timeout, configService); }
            ]
        }

    	public link: ($scope: portal.IDatabasesDirScope, element: JQuery, attributes: any) => any
    	public templateUrl: string
    	public restrict: string

        private configService: portal.ConfigService
        private timeout: ng.ITimeoutService
        private myScope: ng.IScope

    	constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService) {
            this.configService = configService
            this.timeout = $timeout
	        this.templateUrl = '/public/partials/templates/databases.html'
	        this.restrict = 'E'
	        this.link = ($scope: portal.IDatabasesDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
	        
    	}

    	linkFn($scope: portal.IDatabasesDirScope, element: JQuery, attributes: any): any {
            $scope.databasesvm = this
            this.myScope = $scope
        }
    }

}