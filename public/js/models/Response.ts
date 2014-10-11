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
    
    // describes the response log message
    export class ResponseLog {
        constructor(
            public timeStamp: Date,
            public message: string,
            public origin: string){}
    }
    
    // describe the status response
    export class StatusResponse {
        constructor(
            public lastUpdate: string,
            public lastError: string,
            public isNotFound: boolean,
            public isInvalid: boolean){}
    }
    
}