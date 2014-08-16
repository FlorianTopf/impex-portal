'use strict'

describe('PortalCtrl', function() {
	var path= '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var scope, timeout, cService, state, growlService, $httpBackend;
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, $timeout, configService, 
			$controller, $state, growl, _$httpBackend_) {

		scope = $rootScope.$new();
		timeout = $timeout
		cService = configService;
		state = $state
		growlService = growl
		$httpBackend = _$httpBackend_
		
		jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock/';
		$httpBackend.when('GET', '/config?&fmt=json').respond(getJSONFixture('config.json'));
		$httpBackend.when('GET', '/userdata').respond(getJSONFixture('userData.json'));
		// just serve an empty result here 
		$httpBackend.when('GET', '/public/partials/portalMap.html').respond('');
		
		$controller('portalCtrl', {$scope: scope, $timeout: timeout, $state: state, growl: growlService});
	}));
	
	it('should inject services', function(){
		expect(scope.vm.configService).toBeDefined();
		expect(scope.vm.timeout).toBeDefined();
		expect(scope.vm.state).toBeDefined();
		expect(scope.vm.growl).toBeDefined();
	});

	it('should init ready value', function(){
		$httpBackend.flush();
		scope.vm.timeout.flush()
		expect(scope.vm.ready).toBeTruthy();
	});
	
	
}); 






