/// <reference path='../_all.ts' />

module portal {
    'use strict';

    // @TODO let's see what the user service needs to have
    export class UserService {
        static $inject: Array<string> = ['$localStorage']

    	public user: User = null
        public localStorage: any = null
        
        public createId(): string {
            return Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1)+
                Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1)
        }

    	constructor($localStorage) {
            this.localStorage = $localStorage
            // initialise needed keys (doesn't overwrite existing ones)
            this.localStorage.$default({
                results: null,
                selections: null
             })
        }

    }
}
