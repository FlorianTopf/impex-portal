'use strict'

// @TODO what about receiving broadcasts?
describe('PortalCtrl', function() {
	var path= '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var scope, window, timeout, cService, mService, rService, state, growlService, regs, $httpBackend;
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, $window, $timeout, configService, 
			methodsService, registryService, $state, growl, $controller, _$httpBackend_) {

		scope = $rootScope.$new();
		timeout = $timeout;
		window = $window
		cService = configService;
		mService = methodsService;
		rService = registryService
		state = $state;
		growlService = growl;
		$httpBackend = _$httpBackend_;
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		regs = getJSONFixture('regions.json');
		$httpBackend.when('GET', '/config?&fmt=json').respond(getJSONFixture('config.json'));
		$httpBackend.when('GET', '/userdata').respond(getJSONFixture('userData.json'));
		$httpBackend.when('GET', '/filter/region').respond(regs);
		$httpBackend.when('GET', '/filter/region/'+regs.data[0]).respond(
				['spase://IMPEX/Repository/FMI/HYB']);
		$httpBackend.when('GET', '/filter/region/'+regs.data[1]).respond(
				['spase://IMPEX/Repository/FMI/HYB','spase://IMPEX/Repository/LATMOS']);
		// init filterMap (usually done in config resolve)
		cService.filterMap = getJSONFixture('filterMap.json');
		// just serve an empty result here 
		$httpBackend.when('GET', '/public/partials/portalMap.html').respond('');

		
		$controller('portalCtrl', {$scope: scope, $window: window, $timeout: timeout, configService: cService, 
			methodsService: mService, registryService: rService, $state: state, growl: growlService});
	}));
	
	it('should inject services', function(){
		expect(scope.vm.window).toBeDefined();
		expect(scope.vm.timeout).toBeDefined();
		expect(scope.vm.configService).toBeDefined();
		//console.log(JSON.stringify(scope.vm.configService));
		expect(scope.vm.methodsService).toBeDefined();
		expect(scope.vm.registryService).toBeDefined();
		expect(scope.vm.state).toBeDefined();
		expect(scope.vm.growl).toBeDefined();
	});
	
	it('should init tooltips', function(){
		expect(scope.vm.servicesTooltip).toBeDefined();
		expect(scope.vm.toolsTooltip).toBeDefined();
		expect(scope.vm.myDataTooltip).toBeDefined();
		expect(scope.vm.filterTooltip).toBeDefined();
	});
	
	it('should init filter values', function(){
		expect(scope.vm.isFilterCollapsed).toBeTruthy();
		expect(scope.vm.isFilterLoading).toBeFalsy();
		expect(scope.vm.registryService.selectedFilter).toBeDefined();
		// filterMap must be reset at init
		for(var e in scope.vm.configService.filterMap) {
			expect(scope.vm.configService.filterMap[e]).toBeTruthy();
		}
		// filter must be set false on init
		regs.data.forEach(function(r) {
			expect(scope.vm.registryService.selectedFilter[r]).toBeFalsy();
		});
	})

	it('should init ready value', function(){
		$httpBackend.flush();
		scope.vm.timeout.flush();
		expect(scope.vm.ready).toBeTruthy();
	});
	
	it('should select and deselect filter', function(){
		scope.vm.selectFilter(regs.data[0]);
		expect(scope.vm.registryService.selectedFilter[regs.data[0]]).toBeTruthy();
		scope.vm.selectFilter(regs.data[0]);
		expect(scope.vm.registryService.selectedFilter[regs.data[0]]).toBeFalsy();
	});
	
	it('should request and set filters', function(){
		scope.vm.selectFilter(regs.data[0]);
		expect(scope.vm.registryService.selectedFilter[regs.data[0]]).toBeTruthy();
		scope.vm.selectFilter(regs.data[1]);
		expect(scope.vm.registryService.selectedFilter[regs.data[1]]).toBeTruthy();
		scope.vm.requestFilter();
        expect(scope.vm.isFilterCollapsed).toBeTruthy();
        expect(scope.vm.isFilterLoading).toBeTruthy();
        expect(scope.vm.registryService.isFilterSet).toBeFalsy();
		$httpBackend.flush();
        expect(scope.vm.isFilterLoading).toBeFalsy();
        expect(scope.vm.registryService.isFilterSet).toBeTruthy();
        // @TODO we should test if the filterMap is correctly set
        expect(scope.vm.configService.filterMap['spase://IMPEX/Repository/FMI/HYB']).toBeTruthy();
        expect(scope.vm.configService.filterMap['spase://IMPEX/Repository/LATMOS']).toBeTruthy();
	});
	
}); 






