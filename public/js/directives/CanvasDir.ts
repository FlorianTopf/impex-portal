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
            
            //@TODO we need to a some broadcaster watcher here
            $scope.$on('database-success', (e, id: string) => {
                var dbName = this.configService.getDatabase(id).name
                this.activeDatabase = dbName
                this.drawDatabasePath(dbName)
            })
            
            $scope.$on('clear-paths', (e) => {
                this.activeDatabase = null
                this.activeService = null
                this.clear()
            })
            
        }

        private handleResize(element: JQuery) {
            this.main = $("#main").offset()
            element.offset(this.main)
            element.css("zIndex", "0")

            this.height = $("#main").height()+1
            this.width = $("#main").width()+1
            $("#canvas").height(this.height)
            $("#canvas").width(this.width)
            
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            canvas.height = this.height
            canvas.width = this.width
            
            // just for testing
            //this.activeDatabase = 'SINP'
            
            if(this.activeDatabase) {
                this.drawDatabasePath(this.activeDatabase)  
            } else if(this.activeService) {
            
            
            }
            //this.timeout(() => { this.toggleCanvas(true) }, 1000)
            //this.timeout(() => { this.toggleCanvas(false) }, 2000)
        }
        
        private drawDatabasePath(name: string) {
            this.database = $("#"+this.activeDatabase+"-database").offset()
            this.elemH = $("#"+this.activeDatabase+"-database").outerHeight(true)
            this.elemW = $("#"+this.activeDatabase+"-database").outerWidth(true)
            this.service = $("#"+this.activeDatabase+"-service").offset()
            this.myData = $('#MY-DATA').offset() 
            
            // clear canvas before
            this.clear()
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            var ctx = canvas.getContext('2d')
            ctx.lineWidth = 2
            ctx.strokeStyle = "#000000"
            ctx.beginPath()
            // testing path from database to my data and services
            // line top down + arrow
            ctx.moveTo(this.database.left-this.main.left+this.elemW/2, this.database.top-this.main.top+this.elemH)
            ctx.lineTo(this.database.left-this.main.left+this.elemW/2, this.service.top-this.main.top)
            ctx.lineTo(this.database.left-this.main.left+this.elemW/2-10, this.service.top-this.main.top-10)
            ctx.moveTo(this.database.left-this.main.left+this.elemW/2, this.service.top-this.main.top)
            ctx.lineTo(this.database.left-this.main.left+this.elemW/2+10, this.service.top-this.main.top-10)
            // line to left + arrow
            ctx.moveTo(this.database.left-this.main.left+this.elemW/2, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-15, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-5, this.myData.top-this.main.top+this.elemH/2-10)
            ctx.moveTo(this.myData.left-this.main.left+this.elemW-15, this.myData.top-this.main.top+this.elemH/2)
            ctx.lineTo(this.myData.left-this.main.left+this.elemW-5, this.myData.top-this.main.top+this.elemH/2+10)
            ctx.stroke()
        }
        
        private drawServicePath(name: string) {
        
        }
        

        private clear() {
            var canvas = <HTMLCanvasElement>document.getElementById('canvas')
            if(canvas) {
                var ctx = canvas.getContext('2d')
                ctx.clearRect(0, 0, canvas.width, canvas.height)
            }
        }


        private toggleCanvas(front: boolean) {
            if(front) {
                $("#canvas").hide()
            } else {
                $("#canvas").show()
            }
        }

    }

}