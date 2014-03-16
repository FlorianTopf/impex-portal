/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export class ConfigService {
         public config: Config = null
         public url: string = '/'
        
         constructor() {}
        
        getConfigUrl(): string {
            return this.url+'config?fmt=json'
        }

    }
}
