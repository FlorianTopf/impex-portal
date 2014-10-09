exports.config = {	  
  allScriptsTimeout: 11000,	
		
  seleniumAddress: 'http://localhost:4444/wd/hub',
  
  baseUrl: 'http://localhost:9000/',

  multiCapabilities: [{
	  'browserName': 'chrome'
	}, {
	  'browserName': 'firefox'
  }],
	  
  specs: [
      '../e2e-test/*.specs.js'
  ],

  framework: 'jasmine',

  jasmineNodeOpts: {
	    showColors: true,
	    defaultTimeoutInterval: 30000
  }
};