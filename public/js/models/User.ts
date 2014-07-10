/// <reference path='../_all.ts' />

module portal {
    'use strict';

    // currently saved results
    export interface IResultsMap {
        [id: string]: IResponse 
    }
    
    // currently saved selections
    export class Selection {
        constructor(
            public id: string,
            public type: string,
            public elem: SpaseElem) {}
    }    
    
    
    // @TODO let's see what the user object needs to have
    export class User {
        public id: string
        // maybe make array out of it (chronological)
        public results: IResultsMap
        public selections: Array<Selection>
  
        constructor(id: string){
            this.id = id
            this.results = {}
            this.selections = []
        }
    }
}

