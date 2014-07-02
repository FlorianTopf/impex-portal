/// <reference path='../_all.ts' />

module portal {
    'use strict';

    // @TODO let's see what the user service needs to have
    export class UserService {

    	public user: User = null
        
        public createId(): string {
            return Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1)
        }

    	constructor() {}

    }
}
