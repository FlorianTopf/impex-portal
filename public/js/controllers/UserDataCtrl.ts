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
        private upload: any
        
        public initialising: boolean = false
        public status: string
        public showError: boolean = false  
        
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
        
        public onFileSelect($files) {
            //$files: an array of files selected, each file has name, size, and type.
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                this.upload = this.uploader.upload({
                    url: '/userdata', //upload.php script, node.js route, or servlet url
                    method: 'POST', // or 'PUT'
                    //headers: {'header-key': 'header-value'},
                    //withCredentials: true,
                    file: file, // or list of files ($files) for html5 only
                    //fileName: 'doc.jpg' or ['1.jpg', '2.jpg', ...] // to modify the name of the file(s)
                    // customize file formData name ('Content-Desposition'), server side file variable name. 
                    //fileFormDataName: file, //or a list of names for multiple files (html5). Default is 'file' 
                    // customize how data is added to formData. See #40#issuecomment-28612000 for sample code
                    //formDataAppender: function(formData, key, val){}
                }).progress(function(evt) {
                    console.log('percent: ' + 100.0 * evt.loaded / evt.total)
                }).success(function(data, status, headers, config) {
                    // file is uploaded successfully
                    console.log(data)
                })
                //.error(...)
                //.then(success, error, progress); 
                // access or attach event listeners to the underlying XMLHttpRequest.
                //.xhr(function(xhr){xhr.upload.addEventListener(...)})
            }
            /* alternative way of uploading, send the file binary with the file's content-type.
            Could be used to upload files to CouchDB, imgur, etc... html5 FileReader is needed. 
            It could also be used to monitor the progress of a normal http post/put request with large data*/
            // $scope.upload = $upload.http({...})  see 88#issuecomment-31366487 for sample code.
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