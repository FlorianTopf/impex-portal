/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes an angular resource
    export interface ISwagger extends ng.resource.IResource<ISwagger> {
        apiVersion: string
        swaggerVersion: string
        basepath: string
        resourcePath: string
        produces: Array<string>
        apis: Array<Api>
        // not needed at IMPEx
        info?: any
    }
    
    export class Api {
        constructor(
            public path: string,
            public operations: Array<Operation>){} 
    }
    
    export class Operation {        
        constructor(
            public method: string,
            public summary: string,
            public notes: string,
            public type: string,
            public nickname: string,
            // not needed at IMPEx
            public authorisations: any,
            public parameters: Array<Parameter>,
            public responseMessages: Array<ResponseMessage>){}
    }
    
    export class Parameter {        
        constructor(
            public name: string,
            public description: string,
            public defaultValue: string,
            public required: boolean,
            public type: string,
            public paramType: string,
            public allowMultiple: boolean
            // @FIXME does not work (enum is a TypeScript keyword)
            //public enum?: Array<string>
            ){}
    }
    
    export class ResponseMessage {
        constructor(
            public code: string,
            public message : string){}
    }

}