/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes a generic response resource
    export interface IResponse extends ng.resource.IResource<IResponse> {
            code: string
            // can be a string or an object
            message: any 
            request?: Object
    }
    
}