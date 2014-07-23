/// <reference path='../_all.ts' />

module portal {
    'use strict'; 

    // describes an angular resource
    export interface IConfig extends ng.resource.IResource<IConfig> {
      databases: Array<Database>
      tools: Array<Tool>
    }
    
    export class Database {
        constructor(
            public id: string,
            public type: string,
            public name: string, 
            public description: string,
            public dns: Array<string>,
            public methods: Array<string>,
            public tree: Array<string>,
            public protocol: Array<string>,
            public info: string){} 
    }
    
    export class Tool {
        constructor(
            public name: string,
            public description: string,
            public url: Array<string>,
            public info: string){}
    }
}
