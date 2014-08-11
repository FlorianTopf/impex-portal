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
        deleteUserData(): string
    }
    
    export class UserService {
        static $inject: Array<string> = ['$localStorage', '$sessionStorage', '$resource']

        private resource: ng.resource.IResourceService
        private url: string = '/'
    	public user: User = null
        public localStorage: any = null
        public sessionStorage: any = null

         // creates an action descriptor for list
        private userListAction: ng.resource.IActionDescriptor = {
            method: 'GET',
            isArray: true
        }  
        
         // creates an action descriptor for delete
        private userDeleteAction: ng.resource.IActionDescriptor = {
            method: 'DELETE'
        }
        
    	constructor($localStorage, $sessionStorage, $resource) {
            this.localStorage = $localStorage
            this.sessionStorage = $sessionStorage
            // initialise needed keys (doesn't overwrite existing ones)
            this.localStorage.$default({
                results: null,
                selections: null
            })
            // saves current method state
            this.sessionStorage.$default({
                methods: {}
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
        
        // returns the resource handler for userdata
        public UserData(): IUserResource {
            return <IUserResource>this.resource(this.url+'userdata/:name', { name: '@name' },
                { listUserData: this.userListAction, 
                  deleteUserData: this.userDeleteAction 
                })
        } 
        
        // returns promise for load resources
        public loadUserData(): ng.IPromise<ng.resource.IResourceArray<IUserData>> {
            return this.UserData().query().$promise
        }
        
        // calls delete on a specific userdata file
        public deleteUserData(name: string): IUserData {
            return this.UserData().delete({}, { 'name': name })
        }
        
    }
}
