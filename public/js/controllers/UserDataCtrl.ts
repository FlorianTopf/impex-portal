/// <reference path='../_all.ts' />

module portal {
    'use strict';

    export interface IUserDataScope extends ng.IScope {
        datavm: UserDataCtrl;
    }

    // @TODO introduce error/offline handling later
    export class UserDataCtrl {
        private scope: portal.IUserDataScope
        private location: ng.ILocationService
        private timeout: ng.ITimeoutService
        private window: ng.IWindowService
        private userService: portal.UserService 
        private state: ng.ui.IStateService
        private modalInstance: any
        private uploader: any
        private upload: Array<any> = []
        
        public initialising: boolean = false
        public status: string
        public showError: boolean = false
        public selectedFiles: Array<any> = []  
        public progress: Array<number> = []
        
        static $inject: Array<string> = ['$scope', '$location', '$timeout', '$window',
            'userService', '$state', '$modalInstance', '$upload']

        constructor($scope: IUserDataScope, $location: ng.ILocationService, $timeout: ng.ITimeoutService, 
            $window: ng.IWindowService, userService: portal.UserService,
            $state: ng.ui.IStateService, $modalInstance: any, $upload: any) {   
            this.scope = $scope
            $scope.datavm = this
            this.location = $location
            this.timeout = $timeout   
            this.window = $window
            this.userService = userService
            this.state = $state
            this.modalInstance = $modalInstance
            this.uploader = $upload
        }
        
        // @TODO add drag over later
        // see: https://github.com/danialfarid/angular-file-upload/blob/master/demo/war/js/upload.js
        public onFileSelect($files) {
            this.selectedFiles = $files
            this.progress = []
            this.showError = false
            if (this.upload && this.upload.length > 0) {
                for (var i=0; i < this.upload.length; i++) {
                    if (this.upload[i] != null) {
                        this.upload[i].abort()
                    }
                }
            }
            for (var i=0; i < $files.length; i++) {
                this.handleUpload(i)
            }
        }
        
        public handleUpload(i: number) {
            this.progress[i] = 0
            this.upload[i] = this.uploader.upload({
                url: '/userdata', 
                method: 'POST', 
                file: this.selectedFiles[i], 
                fileFormDataName: 'votable'
            })
                    
            this.upload[i].success((response) => {
                this.timeout(() => {
                    var votable = <IUserData>response
                    // adding the info of the posted votable to userservice
                    this.userService.user.voTables.push(votable)
                    //console.log(JSON.stringify(this.userService.user.voTables))
                    this.scope.$broadcast('update-votables', votable.id)
                })
            }).error((response) => {
                if (response.status > 0) { 
                    this.status = response.status + ': ' + response.data
                    this.showError = true
                }
            }).progress((evt) => {
                this.progress[i] = Math.min(100, 100.0 * evt.loaded / evt.total)
            })
        }
        
        // methods for modal
        public saveData() {
            this.modalInstance.close()
        }
        
        public cancelData() {
            this.modalInstance.dismiss()
 
        }
        
        
    

    }
}