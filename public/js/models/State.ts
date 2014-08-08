/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    // used for the sessionStorage
    // currently saved method state
    export class MethodState {
        constructor(
            public path: string,
            public params: Object) {}
    }
    
    // currently saved registry state
    //export class RegistryState {
    //    constructor(public activeItems: IActiveMap) {}
    //}
 
    export class App implements ng.ui.IState {
        public name: string = 'app'
        public abstract: boolean = true
        //public url: string = ''
        public controller: any = ConfigCtrl
        public template: string = '<ui-view/>'
        //public templateUrl: string =  '/public/partials/config.html'
        public resolve: Object = {
            config: ['configService', (ConfigService) => {
                return ConfigService.loadConfig()
            }],
            userData: ['userService', (UserService) => {
                return UserService.loadUserData()
            }]
        }
        
        constructor(){}
    }
    
    export class Portal implements ng.ui.IState {
        public name: string = 'app.portal'
        public url: string = '/portal'
        public templateUrl: string = '/public/partials/portalMap.html'
        public controller: any = PortalCtrl
        
        constructor(){} 
    }
    
    export class Registry implements ng.ui.IState {
        public name = 'app.portal.registry'
        public url: string = '/registry?id'
        
        public onEnter($stateParams, $state, $modal) {
            $modal.open({
                templateUrl: '/public/partials/registryModal.html',
                controller: RegistryCtrl,
                size: 'lg',
                resolve: {
                    id: () => $stateParams.id,
                }
            }).result.then(
                () => { 
                    $state.transitionTo('app.portal') // ok
                }, 
                () => { 
                    $state.transitionTo('app.portal') // cancel
            })
        } 
        
        constructor(){}
    }
    
    export class Methods implements ng.ui.IState {
        public name: string = 'app.portal.methods'
        public url: string = '/methods?id'
        
        public onEnter($stateParams, $state, $modal) {
            $modal.open({
                templateUrl: '/public/partials/methodsModal.html',
                controller: MethodsCtrl,
                size: 'lg',
                resolve: {
                    id: () => $stateParams.id
                }
            }).result.then(
                () => { 
                    $state.transitionTo('app.portal') // ok
                }, 
                () => { 
                    $state.transitionTo('app.portal') // cancel
            })
        }
        
        constructor(){}
    }
    
    export class MyData implements ng.ui.IState {
        public name: string = 'app.portal.userdata'
        public url: string = '/userdata'
    
        public onEnter($stateParams, $state, $modal) {
            $modal.open({
                templateUrl: '/public/partials/userDataModal.html',
                controller: UserDataCtrl,
                size: 'lg',
            }).result.then(
                () => { 
                    $state.transitionTo('app.portal') // ok
                }, 
                () => { 
                    $state.transitionTo('app.portal') // cancel
            })
        }
        
        constructor(){}
    
    }
    
    export class Databases implements ng.ui.IState {
        public name: string = 'app.databases'
        public url: string =  '/databases'
        public templateUrl: string =  '/public/partials/databaseMap.html'
        public controller: any = PortalCtrl
    
        constructor(){}
    
    }
    
}