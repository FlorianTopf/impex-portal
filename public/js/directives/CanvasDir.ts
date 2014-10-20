/// <reference path='../_all.ts' />

module portal {
    'use strict';
    
    export interface ICanvasDirScope extends ng.IScope {
       canvasdirvm: CanvasDir
    }

    export class CanvasDir implements ng.IDirective {

        public injection(): any[] {
            return [
                '$timeout',
                'configService',
                ($timeout: ng.ITimeoutService, configService: portal.ConfigService) => { 
                    return new CanvasDir($timeout, configService); }
            ]
        }

        public link: ($scope: portal.ICanvasDirScope, element: JQuery, attributes: any) => any
        public template: string
        public restrict: string

        // active path from database
        private activeDatabase: string = null
        // active path from service
        private activeService: string = null
        private width: number
        private height: number
        private elemH: number
        private elemW: number
        private main: JQueryCoordinates
        private database: JQueryCoordinates
        private service: JQueryCoordinates
        private myData: JQueryCoordinates
        private tools: JQueryCoordinates

        private myScope: ICanvasDirScope
        private configService: portal.ConfigService
        private timeout: ng.ITimeoutService

        constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService) {
            this.configService = configService
            this.timeout = $timeout

            this.template = '<canvas id="canvas"></canvas>'
            this.restrict = 'E'
            this.link = ($scope: portal.ICanvasDirScope, element: JQuery, attributes: ng.IAttributes) => 
                this.linkFn($scope, element, attributes)
        }

        linkFn($scope: portal.ICanvasDirScope, element: JQuery, attributes: any): any {
            this.myScope = $scope
            $scope.canvasdirvm = this
            
            // will be called on init too
            this.myScope.$watch('windowWidth', (newVal, oldVal) => {
                this.timeout(() => {             
                    this.clear()
                    this.handleResize(element)
                })
            })
            
            this.myScope.$on('database-success', (e, id: string) => {
                this.clear()
                this.activeService = null
                this.activeDatabase = this.configService.getDatabase(id).name
                this.drawDatabasePath()
            })
            
            this.myScope.$on('service-success', (e, id: string) => {
                this.clear()
                this.activeDatabase = null
                this.activeService = this.configService.getDatabase(id).name
                this.drawServicePath()
            })
            
            this.myScope.$on('clear-paths', (e) => {
                this.clear()
                this.activeDatabase = null
                this.activeService = null
            })
            
            this.myScope.$on('draw-paths', (e) => {
                this.toggleCanvas(true)
                $("#filter-collapse, #samp-collapse").one(
                    'transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd', 
                    () => {
                        this.clear()
                        this.toggleCanvas(false)
                        this.handleResize(element) 
                })
            })
            
        }

        private handleResize(element: JQuery) {
            this.main = $("#main").offset()
            element.offset(this.main)
            element.css("zIndex", "0")

            this.height = $("#main").height()
            this.width = $("#main").width()
            $("#canvas").height(this.height)
            $("#canvas").width(this.width)
            
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            canvas.height = this.height
            canvas.width = this.width
            
            // just for testing
            //this.activeDatabase = 'FMI-HYBRID'
            //this.activeService = 'SINP'
            
            if(this.activeDatabase) {
                this.drawDatabasePath()  
            } 
            if(this.activeService) {
                this.drawServicePath()
            }
        }
        
        private drawDatabasePath() {
            this.main = $("#main").offset()
            this.database = $("#"+this.activeDatabase+"-database").offset()
            this.service = $("#"+this.activeDatabase+"-service").offset()
            this.myData = $('#MY-DATA').offset() 
            this.elemH = $("#"+this.activeDatabase+"-database").outerHeight(true)
            this.elemW = $("#"+this.activeDatabase+"-database").outerWidth(true)
            
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            var ctx = canvas.getContext('2d')
            ctx.lineWidth = 2
            ctx.strokeStyle = "#000000"
            ctx.beginPath()
            // path from database to my data and services
            // line top down + arrow
            ctx.moveTo(this.database.left-this.main.left+this.elemW/2, this.database.top-this.main.top+this.elemH)
            ctx.lineTo(this.database.left-this.main.left+this.elemW/2, this.service.top-this.main.top-5)
            ctx.lineTo(this.database.left-this.main.left+this.elemW/2-10, this.service.top-this.main.top-15)
            ctx.moveTo(this.database.left-this.main.left+this.elemW/2, this.service.top-this.main.top-5)
            ctx.lineTo(this.database.left-this.main.left+this.elemW/2+10, this.service.top-this.main.top-15)
            // line to left + arrow
            ctx.moveTo(this.database.left-this.main.left+this.elemW/2, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW+10, this.myData.top-this.main.top+this.elemH/2-10)
            ctx.moveTo(this.myData.left-this.main.left+this.elemW, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW+10, this.myData.top-this.main.top+this.elemH/2+10)
            ctx.stroke()
        }
        
        private drawServicePath() {
            this.main = $("#main").offset()
            this.service = $("#"+this.activeService+"-service").offset()
            this.myData = $('#MY-DATA').offset() 
            this.tools = $('#TOOLS').offset()
            this.elemH = $("#"+this.activeService+"-database").outerHeight(true)
            this.elemW = $("#"+this.activeService+"-database").outerWidth(true)
            
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            var ctx = canvas.getContext('2d')
            ctx.lineWidth = 2
            ctx.strokeStyle = "#000000"
            ctx.beginPath()
            // line to top and left + arrow
            ctx.moveTo(this.service.left-this.main.left+this.elemW/2, this.service.top-this.main.top)
            ctx.lineTo(this.service.left-this.main.left+this.elemW/2, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW+10, this.myData.top-this.main.top+this.elemH/2-10)
            ctx.moveTo(this.myData.left-this.main.left+this.elemW, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW+10, this.myData.top-this.main.top+this.elemH/2+10)
            // line to right + arrow
            ctx.moveTo(this.service.left-this.main.left+this.elemW/2, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.tools.left-this.main.left, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.tools.left-this.main.left-10, this.myData.top-this.main.top+this.elemH/2-10)
            ctx.moveTo(this.tools.left-this.main.left, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.tools.left-this.main.left-10, this.myData.top-this.main.top+this.elemH/2+10)
            ctx.stroke()  
        }
        
        private clear() {
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            if(canvas) {
                var ctx = canvas.getContext('2d')
                ctx.clearRect(0, 0, canvas.width, canvas.height)
            }
        }

        private toggleCanvas(hide: boolean) {
            if(hide) {
                $("#canvas").hide()
            } else {
                $("#canvas").show()
            }
        }

    }

}