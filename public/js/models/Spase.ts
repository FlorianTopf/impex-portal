/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // describes an angular resource
    export interface ISpase extends ng.resource.IResource<ISpase> {
        version: string
        resources: Array<ISpaseElem>
        lang: string
    }
    
    // this is the only way we can deliver an Array 
    // of different elements atm
    export interface ISpaseElem {
        repository?: Repository
        simulationModel?: SimulationModel
    }

    // base element
    export class SpaseElem {
        constructor(
            public resourceId: string){}
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
    
    // repository element
    export class Repository extends SpaseElem {
        constructor(
            public resourceId: string,
            public resourceHeader: ResourceHeader,
            public accessUrl: string){ super(resourceId) } 
    }
    
    export class ModelVersion {
        constructor(
            public versionId: string,
            public releaseDate: string,
            public description: string){}
    }
    
    export class Versions {
        constructor(
            public modelVersion: ModelVersion){}
    }
    
    // simulation model element
    export class SimulationModel extends SpaseElem {
        constructor(
            public resourceId: string,
            public resourceHeader: ResourceHeader,
            public versions: string,
            public simulationType: string){ super(resourceId) } 
    } 
    
}
