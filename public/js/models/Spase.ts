/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes an angular resource
    export interface ISpase extends ng.resource.IResource<ISpase> {
        version: string
        resources: Array<Repository>
        lang: string
    }
    
    export class Contact {
        constructor(
            public personId: string,
            public role: string){}
    }
    
    export class ResourceHeader {
        constructor(
            public resourceName: string,
            public releaseDate: string,
            public description: string,
            public contact: Array<Contact>,
            public informationUrl: Array<string>){}
    }
    
    export class Repository {
        constructor(
            public resourceId: string,
            public resourceHeader: ResourceHeader,
            public accessUrl: string){} 
    }
}
