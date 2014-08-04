/// <reference path='../_all.ts' />

module portal {
    'use strict';

    // describes an angular resource
    export interface IUserData extends ng.resource.IResource<IUserData> {
        id: string
        url: string
    }
    
    // describes the actions for user
    export interface IUserResource extends ng.resource.IResourceClass<IUserData> {
        listUserData(): Array<IUserData>
    }
    
    export class UserService {
        static $inject: Array<string> = ['$localStorage', '$resource']

        private resource: ng.resource.IResourceService
        private url: string = '/'
    	public user: User = null
        public localStorage: any = null

         // creates an action descriptor
        private userAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: true
        }  
        
    	constructor($localStorage, $resource) {
            this.localStorage = $localStorage
            // initialise needed keys (doesn't overwrite existing ones)
            this.localStorage.$default({
                results: null,
                selections: null
             })
           this.resource = $resource
        }
        
        public createId(): string {
            return Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1)+
                Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1)
        }
        
        // returns the resource handler 
        public listUserData(): IUserResource {
            return <IUserResource>this.resource(this.url+'userdata', {},
                { listUserData: this.userAction })
        } 
        
        // returns promise for resource handler
        public loadUserData(): ng.IPromise<ng.resource.IResourceArray<IUserData>> {
            return this.listUserData().query().$promise
        }
        
        
    }
}
