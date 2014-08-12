/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // currently saved results 
    export class Result {
        constructor(
            public repositoryId: string,
            public id: string,
            public method: string,
            public content: IResponse) {}
    }
    
    // currently saved selections
    export class Selection {
        constructor(
            public repositoryId: string,
            public id: string,
            public type: string,
            public elem: SpaseElem) {}
    }    
        
    export class User {
        public id: string
        public results: Array<Result>
        public selections: Array<Selection>
        public voTables: Array<IUserData>
        public activeSelection: Array<Selection>
        
        constructor(id: string){
            this.id = id
            this.results = []
            this.selections = []
            this.voTables = []
            this.activeSelection = []
        }
        
        
    }
}

