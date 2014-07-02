/// <reference path='../_all.ts' />

module portal {
    'use strict';

    // currently saved results
    export interface IResultsMap {
        [id: string]: IResponse 
    }
    
    // @TODO let's see what the user object needs to have
    // we might need another member for stored selections
    export class User {
        public id: string
        public results: IResultsMap
  
        constructor(id: string){
            this.id = id
            this.results = {}
        }
    }
}

