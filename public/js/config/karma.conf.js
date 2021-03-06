// Karma configuration
// Generated on Tue Jul 08 2014 11:06:58 GMT+0200 (CEST)

module.exports = function(config) {
  config.set({

    // base path, that will be used to resolve files and exclude
    basePath: '',


    // frameworks to use
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      '../libs/jquery.js',
      '../libs/angular.js',
      '../libs/angular-resource.js',
      '../libs/angular-ui-router.js',
      '../libs/angular-mocks.js',
      '../libs/*.js',
      '../app.js',
      '../config/jasmine-jquery.js',
      '../test/*.specs.js',
  	  '../../partials/**/*.html',
      {pattern: '../test/mock/*.json', watched: true, served: true, included: false},
    ],


    // list of files to exclude
    exclude: [
      
    ],
    
    // template preprocessors
    preprocessors: {
    	'../../partials/**/*.html': ['ng-html2js']
    },
  
    ngHtml2JsPreprocessor: {
    	// Paths by default are relative to DISK root, 
    	// so we need to make them relative to this folder
    	cacheIdFromPath : function(filepath) {
    		 return filepath.replace("/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/", "/");
        },
    	// setting this option will create only a single module that contains templates
    	// from all the files, so you can load them all with module('foo')
    	moduleName: 'templates'
    },


    // test results reporter to use
    // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: ['Chrome', 'Firefox'],


    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 60000,


    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false
  });
};
