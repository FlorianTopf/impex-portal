/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // importing urlize function
    declare var urlize: any
    
    // @TODO implement loading visualisation
    export class SelectionDir implements ng.IDirective {
        
        public injection(): any[] {
            return [
                () => { return new SelectionDir() }
            ]
        }
        
        public template: string
        public restrict: string
        public replace: boolean
        public scope: any
        
        constructor() {
            this.template = '<ul>'+
                "<member-dir ng-repeat='(key, elem) in selection' name='key' member='elem' "+
                "ng-if='!(elem | isEmpty)'></member-dir>"+
                '</ul>'
            this.restrict = 'E'
            this.replace = true 
            this.scope = {
                selection: '='
            }
        }
  
    }
    

    export class MemberDir implements ng.IDirective {
        
        public injection(): any[] {
            return [
                '$compile',
                ($compile: ng.ICompileService) => 
                    { return new MemberDir($compile) }
            ]
        }
        
        public link: ($scope: any, element: JQuery, attributes: ng.IAttributes) => any
        public template: string
        public restrict: string
        public replace: boolean
        public scope: any
        
        private compileService: ng.ICompileService
        
        constructor($compile: ng.ICompileService) {
            this.compileService = $compile
            this.template = '<li></li>'
            this.restrict = 'E'
            this.replace = true 
            this.scope = {
                name: '=',
                member: '='
            }
            this.link = ($scope: any, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
        }
        
        linkFn($scope: any, element: JQuery, attributes: ng.IAttributes): any {   
            element.append('<strong>'+this.beautify($scope.name)+'</strong>&nbsp;:&nbsp;')
            if(angular.isArray($scope.member)) {
                angular.forEach($scope.member, (m, i) => {
                    if(angular.isNumber(m) || angular.isString(m)) {
                        element.append(urlize(m, { target: '_blank' })+' ')
                    } else if(angular.isObject(m)) {
                        // this is a really cool hack
                        this.compileService("<br/><selection-dir selection='"+JSON.stringify($scope.member[i])+"'></selection-dir>")
                            ($scope, (cloned?: JQuery, scope?: any): any => { element.append(cloned) })
                    } 
                })    
            } else if(angular.isObject($scope.member)) {
                element.append("<br/><selection-dir selection='member'></selection-dir>")
                this.compileService(element.contents())($scope)
            } else {
                element.append(urlize($scope.member, { target: '_blank' }))
            }
        }
        
        private beautify(str: string): string {
            var str = str.replace('ID', 'Id').replace('URL', 'Url')
            if(str.indexOf('_') != -1) {
                var split = str.split('_')
                str = split[0]+split[1].charAt(0).toUpperCase()+split[1].slice(1)
            }
            var array = str.match(/([A-Z]?[^A-Z]*)/g).slice(0,-1)
            var first = array[0].charAt(0).toUpperCase()+array[0].slice(1)
            array.shift()
            return (first+' '+array.join(' ')).trim()
        }
        
    }
    
}