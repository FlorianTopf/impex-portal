'use strict'

// @TODO add specs for active filters!
describe('RegistryDir', function () {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/';
	
	var $compile, scope, timeout, simDb, obsDb, rService, uService, element, $httpBackend;
	
	beforeEach(module('templates'));
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function(_$compile_, $rootScope, $timeout, 
			registryService, userService, _$httpBackend_) {
		$compile = _$compile_;
		scope = $rootScope.$new();
		timeout = $timeout;
		rService = registryService;
		uService = userService;
		$httpBackend = _$httpBackend_;
		
		// spying on broadcast events
        spyOn(scope, '$broadcast').andCallThrough();
        spyOn(scope, '$on').andCallThrough();
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		simDb = getJSONFixture('simDatabase.json');
		obsDb = getJSONFixture('obsDatabase.json');
		
		// mocking the cache map of the registryService
		rService.cachedElements = {
			'repo-sim123': getJSONFixture('simRepository.json').resources.map(function(r){ return r.repository; }),
			'repo-obs123': getJSONFixture('obsRepository.json').resources.map(function(r){ return r.repository; }),
			'model-123': getJSONFixture('simulationmodel.json').resources.map(function(r){ return r.simulationModel; }),
			'run-123': getJSONFixture('simulationrun.json').resources.map(function(r){ return r.simulationRun; }),
			'output-123': getJSONFixture('numericaloutput.json').resources.map(function(r){ return r.numericalOutput; }),
			'granule-123': getJSONFixture('granule.json').resources.map(function(r){ return r.granule; }),
			'obs-123': getJSONFixture('observatory.json').resources.map(function(r){ return r.observatory; }),
			'instr-123': getJSONFixture('instrument.json').resources.map(function(r){ return r.instrument; }),
			'data-123': getJSONFixture('numericaldata.json').resources.map(function(r){ return r.numericalData; }),
		}
		
		// test element 
		element = angular.element('<registry-dir db="{{db}}"></registry-dir>');
		
	    // timeout.flush() needs empty responses here
		$httpBackend.when('GET', '/config?fmt=json').respond('');
		$httpBackend.when('GET', '/userdata').respond('');
		$httpBackend.when('GET', '/filter/region').respond('');
		
	}));
	
	function createDirective(){
		var template = $compile(element)(scope);
		// setting the db attribute
		scope.db = simDb.id;
		scope.$digest();
		return template;
	}
	
	it('should render the template and init variables', function(){
		var template = createDirective();
		var templateAsHtml = template.html();
		expect(templateAsHtml).toContain('accordion');
        expect(scope.regdirvm.oneAtATime).toBeTruthy();
        expect(scope.regdirvm.showError).toBeFalsy();
        expect(scope.regdirvm.status).toEqual('');
    	expect(scope.regdirvm.repositoryId).toEqual(simDb.id);
		expect(scope.regdirvm.activeItems).toEqual({});
        expect(scope.regdirvm.repositories).toEqual([]);
        expect(scope.regdirvm.simulationModels).toEqual([]);
        expect(scope.regdirvm.simulationRuns).toEqual([]);
        expect(scope.regdirvm.numericalOutputs).toEqual([]);
        expect(scope.regdirvm.granules).toEqual([]);
        expect(scope.regdirvm.observatories).toEqual([]);
        expect(scope.regdirvm.instruments).toEqual([]);
        expect(scope.regdirvm.numericalData).toEqual([]);
	});
	
	it('should react on registry error broadcast', function(){
		var template = createDirective();
		var error = 'no numerical data found';
		scope.$broadcast('registry-error', error);
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.status).toEqual(error);
		expect(scope.regdirvm.showError).toBeTruthy();
	});
	
	
	it('should react on update-repositories', function(){
		var template = createDirective();
		scope.$broadcast('update-repositories', 'repo-sim123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.repositories).toEqual(rService.cachedElements['repo-sim123']);
	});
	
	it('should react on update-simulation-models', function(){
		var template = createDirective();
		scope.$broadcast('update-simulation-models', 'model-123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.simulationModels).toEqual(rService.cachedElements['model-123']);
	});
	
	it('should react on update-simulation-runs', function(){
		var template = createDirective();
		scope.$broadcast('update-simulation-runs', 'run-123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.simulationRuns).toEqual(rService.cachedElements['run-123']);
	});
	
	it('should react on update-numerical-outputs', function(){
		var template = createDirective();
		scope.$broadcast('update-numerical-outputs', 'output-123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.numericalOutputs).toEqual(rService.cachedElements['output-123']);
	});
	
	it('should react on update-granules', function(){
		var template = createDirective();
		scope.$broadcast('update-granules', 'granule-123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.granules).toEqual(rService.cachedElements['granule-123']);
	});
	
	it('should react on update-observatories', function(){
		var template = createDirective();
		scope.$broadcast('update-observatories', 'obs-123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.observatories).toEqual(rService.cachedElements['obs-123']);
	});
	
	it('should react on update-instruments', function(){
		var template = createDirective();
		scope.$broadcast('update-instruments', 'instr-123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.instruments).toEqual(rService.cachedElements['instr-123']);
		
	});
	
	it('should react on update-numerical-data', function(){
		var template = createDirective();
		scope.$broadcast('update-numerical-data', 'data-123');
		expect(scope.$on).toHaveBeenCalled();
		expect(scope.regdirvm.numericalData).toEqual(rService.cachedElements['data-123']);
	});
	
	
	// @TODO finalise clean triggers
	/* it('should react on clear-registry-dir', function(){

		
	});
	
	it('should react on clear-simulation-runs', function(){
		
		
	});
	
	it('should react on clear-numerical-outputs', function(){
		
		
	});
	
	it('should react on clear-granules', function(){
		
		
	});
	
	it('should react on clear-instruments', function(){
		
		
	});
	
	it('should react on clear-numerical-data', function(){
		
		
	});*/
	

});