'use strict'

// @TODO what about receiving broadcasts?
describe('PortalCtrl', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/';
		
	var scope, window, timeout, cService, mService, rService, sService, state, growlService, regs, $httpBackend;
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, $window, $timeout, configService, 
			methodsService, registryService, sampService, $state, growl, $controller, _$httpBackend_) {

		scope = $rootScope.$new();
		timeout = $timeout;
		window = $window;
		cService = configService;
		mService = methodsService;
		rService = registryService;
		sService = sampService;
		state = $state;
		growlService = growl;
		$httpBackend = _$httpBackend_;
		// spying on broadcast events
        spyOn(scope, '$broadcast').andCallThrough();
        // spying on growl warning
        spyOn(growlService, 'warning');
        // spying on samp caller (only against mock .andCallThrough() doesn't work)
        spyOn(sService.connector, 'runWithConnection');
        spyOn(sService.connector, 'unregister');
        
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		regs = getJSONFixture('regions.json');
		$httpBackend.when('GET', '/config?fmt=json').respond(getJSONFixture('config.json'));
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
		expect(scope.vm.http).toBeDefined();
		expect(scope.vm.state).toBeDefined();
		expect(scope.vm.growl).toBeDefined();
		expect(scope.vm.configService).toBeDefined();
		expect(scope.vm.methodsService).toBeDefined();
		expect(scope.vm.registryService).toBeDefined();
		expect(scope.vm.sampService).toBeDefined();
		expect(scope.vm.ready).toBeFalsy();
	});
	
	it('should init tooltips', function(){
		expect(scope.vm.databasesTooltip).toBeDefined();
		expect(scope.vm.servicesTooltip).toBeDefined();
		expect(scope.vm.toolsTooltip).toBeDefined();
		expect(scope.vm.myDataTooltip).toBeDefined();
		expect(scope.vm.filterTooltip).toBeDefined();
		expect(scope.vm.sampTooltip).toBeDefined();
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
	});
	
	it('should init samp values', function(){
		expect(scope.vm.isSampCollapsed).toBeTruthy();
		expect(scope.vm.sampService.clientTracker.onchange).toBeDefined();
	});
	
	it('should init path values', function(){
		expect(scope.vm.activeDatabase).toBeNull();
		expect(scope.vm.activeService).toBeNull();
	});
	
	it('should init feedback values', function(){
        expect(scope.vm.result).toEqual('hidden');
        expect(scope.vm.resultMessage).toBeNull();
        expect(scope.vm.feedbackForm).toBeNull();
        expect(scope.vm.submitted).toBeFalsy();
        expect(scope.vm.submitButtonDisabled).toBeFalsy();
        expect(scope.vm.sendingFeedback).toBeFalsy();
        expect(scope.vm.feedbackTools).toBeDefined();		
	});
	
	it('should init ready value', function(){
		$httpBackend.flush();
		scope.vm.timeout.flush();
		expect(scope.vm.ready).toBeTruthy();
		expect(scope.vm.growl.warning).toHaveBeenCalled();
	});
	
	it('should init selectable filters', function(){
        for(var r in scope.vm.configService.filterRegions)
            expect(scope.vm.registryService.selectedFilter[r]).toBeFalsy();
	});
	
	it('should toogle filter collapsible', function(){
		scope.vm.toggleFilter();
		expect(scope.vm.isFilterCollapsed).toBeFalsy();
		expect(scope.$broadcast).toHaveBeenCalledWith('draw-paths');
	});
	
	it('should select and deselect filter', function(){
		scope.vm.selectFilter(regs.data[0]);
		expect(scope.vm.registryService.selectedFilter[regs.data[0]]).toBeTruthy();
		scope.vm.selectFilter(regs.data[0]);
		expect(scope.vm.registryService.selectedFilter[regs.data[0]]).toBeFalsy();
	});
	
	it('should reset filter', function(){
		scope.vm.selectFilter(regs.data[0]);
		scope.vm.selectFilter(regs.data[1]);
		scope.vm.selectFilter(regs.data[2]);
		scope.vm.resetFilter();
		expect(scope.vm.registryService.selectedFilter[regs.data[0]]).toBeFalsy();
		expect(scope.vm.registryService.selectedFilter[regs.data[1]]).toBeFalsy();
		expect(scope.vm.registryService.selectedFilter[regs.data[2]]).toBeFalsy();
        for(var id in scope.vm.configService.filterMap) {
            expect(scope.vm.configService.filterMap[id]).toBeTruthy();
        }
        expect(scope.vm.registryService.isFilterSet).toBeFalsy();
        expect(scope.vm.isFilterSelected).toBeFalsy();
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
        expect(scope.vm.configService.filterMap['spase://IMPEX/Repository/FMI/HYB']).toBeTruthy();
        expect(scope.vm.configService.filterMap['spase://IMPEX/Repository/LATMOS']).toBeTruthy();
	});
	
	it('should toggle samp collapsible', function(){
		scope.vm.toggleSamp();
		expect(scope.vm.isSampCollapsed).toBeFalsy();
		if(scope.vm.isFilterSelected)
			expect(scope.$broadcast).toHaveBeenCalledWith('draw-paths');
	});
	
	// we must start a hub before doing this test!
	it('should register on samp hub', function(){
		scope.vm.registerSamp();
		//expect(scope.vm.window.isSampRegistered).toBeTruthy(); 
		expect(scope.vm.growl.warning).toHaveBeenCalled();
		expect(scope.vm.sampService.connector.runWithConnection).toHaveBeenCalled();
	})
	
	// we must start a hub before doing this test!
	it('should unregister on samp hub', function(){
		scope.vm.unregisterSamp();
		expect(scope.vm.sampService.connector.unregister).toHaveBeenCalled();
		expect(scope.vm.window.isSampRegistered).toBeFalsy();
		expect(scope.vm.sampService.clients).toEqual({});
	})
	
	// @TODO test unregister on beforeunload event
	// (full page reload is not allowed)
	it('should unregister samp on beforeunload event', function(){
		spyOn(window, 'onbeforeunload');
        //spyOn(scope.vm, 'unregisterSamp');
		// trigger the event
        scope.vm.window.onbeforeunload();

		//expect(scope.vm.window.onbeforeunload).toHaveBeenCalled();
		//expect(scope.vm.unregisterSamp).toHaveBeenCalled();
		expect(scope.vm.window.isSampRegistered).toBeFalsy(); 
		expect(scope.vm.sampService.clients).toEqual({});
	});
	
	// @TODO testing monitoring with onchange event
	
	it('should reset an active path', function(){
		scope.vm.resetPath();
        expect(scope.vm.activeDatabase).toBeNull();
        expect(scope.vm.activeService).toBeNull();
        expect(scope.$broadcast).toHaveBeenCalledWith('clear-paths');
		
	});
	
	// @TODO testing the feedback form submission
	
}); 






