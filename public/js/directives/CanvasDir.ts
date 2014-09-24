/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export class CanvasDir implements ng.IDirective {

        public injection(): any[] {
            return [
                '$timeout',
                'configService',
                ($timeout: ng.ITimeoutService, configService: portal.ConfigService) => { 
                    return new CanvasDir($timeout, configService); }
            ]
        }

        public link: ($scope: ng.IScope, element: JQuery, attributes: any) => any
        public template: string
        public restrict: string

        private width: number
        private height: number
        private elemH: number
        private elemW: number
        // active path from database
        private activeDatabase: string = null
        // active path from service
        private activeService: string = null
        private main: JQueryCoordinates
        private database: JQueryCoordinates
        private service: JQueryCoordinates
        private myData: JQueryCoordinates
        private tools: JQueryCoordinates

        private configService: portal.ConfigService
        private timeout: ng.ITimeoutService

        constructor($timeout: ng.ITimeoutService, configService: portal.ConfigService) {
            this.configService = configService
            this.timeout = $timeout

            this.template = '<canvas id="canvas"></canvas>'
            this.restrict = 'E'
            this.link = ($scope: ng.IScope, element: JQuery, attributes: ng.IAttributes) => this.linkFn($scope, element, attributes)
        }

        linkFn($scope: ng.IScope, element: JQuery, attributes: any): any {
            $scope.$watch('windowWidth', (newVal, oldVal) => {
                this.timeout(() => this.handleResize(element))
            })
            
            $scope.$on('database-success', (e, id: string) => {
                this.activeDatabase = this.configService.getDatabase(id).name
                this.drawDatabasePath()
            })
            
            $scope.$on('service-success', (e, id: string) => {
                this.activeService = this.configService.getDatabase(id).name
                this.drawServicePath()
            })
            
            $scope.$on('clear-paths', (e) => {
                this.activeDatabase = null
                this.activeService = null
                this.clear()
            })
            
            $scope.$on('draw-paths', (e) => {
                this.toggleCanvas(true)
                $("#filter-collapse, #samp-collapse").one(
                    'transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd', 
                    () => {
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
            } else if(this.activeService) {
                this.drawServicePath()
            
            }
        }
        
        private drawDatabasePath() {
            this.database = $("#"+this.activeDatabase+"-database").offset()
            this.elemH = $("#"+this.activeDatabase+"-database").outerHeight(true)
            this.elemW = $("#"+this.activeDatabase+"-database").outerWidth(true)
            this.service = $("#"+this.activeDatabase+"-service").offset()
            this.myData = $('#MY-DATA').offset() 
            //console.log(JSON.stringify(this.myData)+' '+this.elemH+' '+this.elemW)
            // clear canvas before
            this.clear()
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
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-15, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-5, this.myData.top-this.main.top+this.elemH/2-10)
            ctx.moveTo(this.myData.left-this.main.left+this.elemW-15, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-5, this.myData.top-this.main.top+this.elemH/2+10)
            ctx.stroke()
        }
        
        private drawServicePath() {
            this.service = $("#"+this.activeService+"-service").offset()
            this.elemH = $("#"+this.activeService+"-database").outerHeight(true)
            this.elemW = $("#"+this.activeService+"-database").outerWidth(true)
            this.tools = $('#TOOLS').offset()
            this.myData = $('#MY-DATA').offset() 
            // clear canvas before
            this.clear()
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            var ctx = canvas.getContext('2d')
            ctx.lineWidth = 2
            ctx.strokeStyle = "#000000"
            ctx.beginPath()
            // line to top and left + arrow
            ctx.moveTo(this.service.left-this.main.left+this.elemW/2, this.service.top-this.main.top)
            ctx.lineTo(this.service.left-this.main.left+this.elemW/2, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-15, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-5, this.myData.top-this.main.top+this.elemH/2-10)
            ctx.moveTo(this.myData.left-this.main.left+this.elemW-15, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-5, this.myData.top-this.main.top+this.elemH/2+10)
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