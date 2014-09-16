/// <reference path='../_all.ts' />

module portal {
    'use strict';
     
    // needed for external SAMP lib
    declare var samp: any
    
    export class SampService {
        static $inject: Array<string> = ['$window']
        // we must use any here (to access global vars)
        private window: any
        public clientTracker: any = null
        public connector: any = null
        public callHandler: any = null
        
        constructor($window: ng.IWindowService){
           this.window = $window
           this.clientTracker = new samp.ClientTracker()
           this.callHandler = this.clientTracker.callHandler
           // generic call handler
           this.callHandler['samp.app.ping'] = (senderId, message, isCall) => {
               console.log('Ping '+senderId+' '+JSON.stringify(message))
           }
           this.callHandler['samp.hub.disconnect'] = (senderId, message) => {
               console.log('Disconnect '+senderId+' '+JSON.stringify(message))
               this.window.isSampRegistered = false 
           }
           this.callHandler['samp.hub.event.shutdown'] = (senderId, message) => {
               console.log('Shutdown '+senderId+' '+JSON.stringify(message))
               this.window.isSampRegistered = false
           }
            
           var baseUrl = this.window.location.href.toString()
           .replace(new RegExp('[^/]*$'), '').replace('#/','')
            var meta = {
                'samp.name': 'IMPExPortal',
                'samp.description': 'Simple VOTable distributor',
                'samp.icon.url': baseUrl + 'public/img/clientIcon.gif',
                'author.mail': 'florian.topf@gmail.com',
                'author.name': 'Florian Topf'
            }
            var subs = this.clientTracker.calculateSubscriptions()
            // init connection
            this.connector = new samp.Connector("IMPExPortal", meta, this.clientTracker, subs)
        }
        
  
    }

}