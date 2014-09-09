/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface IMethodsDirScope extends ng.IScope {
       methdirvm: MethodsDir
    }
    
    // for getVOTableURL form
    export interface IMetadata {
        name: string
        value: string
    }
    
    export class MethodsDir implements ng.IDirective {

        public injection(): any[] {
            return [
                'methodsService',
                'userService',
                (methodsService: portal.MethodsService, userService: portal.UserService) => { 
                    return new MethodsDir(methodsService, userService) }
            ]
        }
        
        public link: ($scope: portal.IMethodsDirScope, element: JQuery, attributes: ng.IAttributes) => any
        public templateUrl: string
        public restrict: string
        
        public repositoryId: string = null
        public method: Api = null
        public request: Object = {}
        public votableRows: Array<Array<string>> = []
        public votableColumns: number = null
        public votableMetadata: Array<Array<IMetadata>> = []
        public selected: Array<IMetadata> = []
        
        private myScope: IMethodsDirScope
        private methodsService: portal.MethodsService
        private userService: portal.UserService

        constructor(methodsService: portal.MethodsService, userService: portal.UserService) {
            this.methodsService = methodsService
            this.userService = userService
            this.templateUrl = '/public/partials/templates/methodsDir.html'
            this.restrict = 'E'
            this.link = ($scope: portal.IMethodsDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
        }

        linkFn($scope: portal.IMethodsDirScope, element: JQuery, attributes: ng.IAttributes): any {
            this.myScope = $scope
            $scope.methdirvm = this
            
            attributes.$observe('db', (id?: string)  => { 
                this.repositoryId = id
            })
            
            this.myScope.$on('set-method-active', (e, method: Api) => {
                this.setMethod(method)
            })
            
            this.myScope.$on('reset-method-request', (e) => {
                this.resetRequest()  
            })
            
            this.myScope.$watch('$includeContentLoaded', (e) => {
                //console.log("MethodsDir loaded")
                if(!(this.repositoryId in this.userService.sessionStorage.methods)) {
                    this.method = null
                    this.request = {}
                }
            })
        }
        
        private setMethod(method: Api) {
            this.method = method
            // reset the request object
            this.request = {}
            // there is only one operation per method
            this.method.operations[0].parameters.forEach((p) => {
                this.request[p.name] = p.defaultValue     
            })
            // refresh session storage
            if(this.repositoryId in this.userService.sessionStorage.methods) {
                if(this.userService.sessionStorage.methods[this.repositoryId].path == this.method.path) {
                    this.request = this.userService.sessionStorage.methods[this.repositoryId].params
                    // just updating the votableColumns if fields are present in the request
                    if('Fields' in this.request) {
                        this.votableColumns = this.request['Fields'].length
                        var rows = this.request['Fields'][0].data.length
                        for(var j=0; j<rows; j++)
                            this.votableRows[j] = []
                        for(var i=0; i<this.votableColumns; i++) {
                            this.votableMetadata[i] = [
                                {name:'name', value: this.request['Fields'][i].name}, 
                                {name:'ID', value: this.request['Fields'][i].ID},
                                {name:'unit', value: this.request['Fields'][i].unit}, 
                                {name:'datatype', value: this.request['Fields'][i].datatype}, 
                                {name:'ucd', value:  this.request['Fields'][i].ucd}]
                            this.selected[i] = this.votableMetadata[i][0]
                            for(var j=0; j<rows; j++)
                                this.votableRows[j].push(this.request['Fields'][i].data[j])
                        } 
                    }
                } else {
                    this.userService.sessionStorage.methods[this.repositoryId] = 
                        new MethodState(method.path, this.request) 
                }
            } else {
                this.userService.sessionStorage.methods[this.repositoryId] = 
                    new MethodState(method.path, this.request)
            }
            
            // check if there is an id field and broadcast applyable elements
            if(this.method.operations[0].parameters.filter((e) => e.name == 'id').length != 0) {
                // there is only one id param per method 
                var param = this.method.operations[0].parameters.filter((e) => e.name == 'id')[0]
                this.myScope.$broadcast('set-applyable-elements', param.description)
            } else {
                // if there is no id, broadcast empty string
                this.myScope.$broadcast('set-applyable-elements', '')
            }
            
            // check if there is an url field and broadcast indication
            if(this.method.operations[0].parameters.filter((e) => e.name == 'votable_url').length != 0) {
                this.myScope.$broadcast('set-applyable-votable', true)
            } else {
                // if there is no url field return false
                this.myScope.$broadcast('set-applyable-votable', false)
            }
            
            // if there is a SINP method chosen, we must forward info about applyable models
            if(this.method.path.indexOf('SINP') != -1) {
               //console.log(this.currentMethod.operations[0].nickname)
               for(var key in this.methodsService.applyableModels) {
                    var methods = this.methodsService.applyableModels[key]
                    var index = methods.indexOf(this.method.operations[0].nickname)
                    if(index != -1) this.myScope.$broadcast('set-applyable-models', key) 
               }
            }
            
        }
        
        public resetRequest() {
            // reset the request object
            this.request = {}
            // there is only one operation per method
            this.method.operations[0].parameters.forEach((p) => {
                this.request[p.name] = p.defaultValue     
            })
            // savely reset VOTableURL form
            this.votableColumns = null
            this.votableRows = []    
            this.votableMetadata = []
            this.selected = []
            this.userService.sessionStorage.methods[this.repositoryId].params = this.request
        }
       
        // method for applying a selection to the current method
        public applySelection(resourceId: string) {
            //console.log("applySelection "+resourceId)
            this.request['id'] = resourceId
        }
        
        // method for applying a votable url to the current method
        public applyVOTable(url: string) {
            //console.log("applyVOTable "+url)
            this.request['votable_url'] = url
        }
        
        public updateRequest(paramName: string) {            
            this.userService.sessionStorage.methods[this.repositoryId]
                .params[paramName] = this.request[paramName]
        }
        
        public updateRequestDate(paramName: string) {
            if(paramName in this.request) {
                var iso = new Date(this.request[paramName])
                this.request[paramName] = iso.toISOString()
                this.userService.sessionStorage.methods[this.repositoryId]
                    .params[paramName] = this.request[paramName]
            }
        }
        
        // used for getVOTableURL form
        public refreshVotableHeader() {
            if(angular.isNumber(this.votableColumns)) {
                for(var i = 0; i < this.votableColumns; i++) {
                    this.votableMetadata[i] = [
                        {name:'name', value:''}, 
                        {name:'ID', value: ''},
                        {name:'unit', value:''}, 
                        {name:'datatype', value:''}, 
                        {name:'ucd', value:''}]
                    this.selected[i] = null
                }
                this.addVotableRow() // just add an empty row
            }
        }
        
        // used for getVOTableURL form
        public updateVotableHeader(index: number) {
            this.votableMetadata[index] = this.votableMetadata[index].map((m) => {
                if(m.name == this.selected[index].name)
                    return this.selected[index]
                else
                    return m
            })
            this.updateVOtableRequest()
        }
        
        // used for getVOTableURL form
        public addVotableRow() {
            var arr = []
            for(var i = 0; i < this.votableColumns; i++) {
               arr[i] = "Field-"+(this.votableRows.length+1)+"-"+(i+1)
            }
            this.votableRows.push(arr)
            this.updateVOtableRequest()
        }
        
        // used for getVOTableURL form
        public deleteVotableRow(index: number) {
            this.votableRows.splice(index, 1)
            if(this.votableRows.length == 0)
                this.addVotableRow() // just add an empty row
            else 
                this.updateVOtableRequest()
        }
        
        // used for getVOTableURL form
        public addVotableColumn() {
            this.votableColumns++
            this.votableRows.forEach((r) => r.push("Field-"+this.votableColumns))
            this.votableMetadata.push([
                {name:'name', value:''}, 
                {name:'ID', value:''},
                {name:'unit', value:''}, 
                {name:'datatype', value:''}, 
                {name:'ucd', value:''}])
            this.selected.push(null)
            this.updateVOtableRequest()
        }
        
        // used for getVOTableURL form
        public deleteVotableColumn(index: number) {
            this.votableColumns--
            if(this.votableColumns == 0) 
                this.resetVotable()  
            else {    
                this.votableRows.forEach((r) => r.splice(index, 1))
                this.votableMetadata.splice(index, 1)
                this.selected.splice(index, 1)
            }
            this.updateVOtableRequest()
        }
        
        // used for getVOTableURL form
        public resetVotable() {
            this.votableColumns = null
            this.votableRows = []    
            this.votableMetadata = []
            this.selected = []
            this.updateVOtableRequest()
        }
        
        // used for getVOTableURL form
        public updateVOtableRequest() {
           // filling the fields
           this.request['Fields'] = this.votableMetadata.map((col, key) => {
                var data: Array<string> = this.votableRows.map((r) => { return r[key] })  
                var result: Object = {}
                result['data'] = data
                col.forEach((i) => { if(i.value != '') result[i.name] = i.value })
                return result
           })
        }
        
    }

}