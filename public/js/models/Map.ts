/// <reference path='../_all.ts' />

module portal {
    'use strict'; 
    
    // string -> boolean map
    export interface IBooleanMap {
        [id: string]: boolean
    }
    
    // string -> array map
    export interface IArrayMap {
        [id: string]: Array<string>
    }
    
    // simple resource map
    export interface IElementMap {
        [id: string]: SpaseElem
    }
    
    // multiple resources map
    export interface IElementArrayMap {
        [id: string]: Array<SpaseElem>
    }
    
    // string -> object map
    export interface IStatusMap {
        [id: string]: StatusResponse
    }

}
