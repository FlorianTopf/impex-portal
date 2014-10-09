'use strict'

describe('RegistryService', function() {
	var path = '/Users/floriantopf/Documents/CAMPUS02/MA-Courses/DAB/impex-portal/public/'
		
	var scope, resource, rService, repos, models, runs, outputs, granule, $httpBackend;
	
	beforeEach(module('templates'));
	
	beforeEach(angular.mock.module('portal'));
	
	beforeEach(angular.mock.inject(function($rootScope, registryService, _$httpBackend_) {
		scope = $rootScope;
		rService = registryService;
		$httpBackend = _$httpBackend_;
		
		// spying on broadcast events
        spyOn(scope, '$broadcast').andCallThrough();
		
        jasmine.getJSONFixtures().fixturesPath=path+'js/test/mock';
		repos = getJSONFixture('simRepository.json');
		
		// @TODO creation needs empty responses here
		$httpBackend.when('GET', '/config?fmt=json').respond('');
		$httpBackend.when('GET', '/userdata').respond('');
		$httpBackend.when('GET', '/filter/region').respond('');
		// return repositories
		$httpBackend.when('GET', '/registry/repository?fmt=json&id=spase:%2F%2FIMPEX%2FRepository%2FFMI%2FHYB').respond(repos);
	}));
	
	
	it('should inject services', function(){
		expect(rService.scope).toBeDefined();
		expect(rService.resource).toBeDefined();
	});
	
	it('should set initial variables', function(){
		expect(rService.url).toEqual('/');
		expect(rService.isFilterSet).toBeFalsy();
		expect(rService.selectedFilter).toBeDefined();
		expect(rService.cachedElements).toBeDefined();
		expect(rService.selectables).toBeDefined();
		expect(rService.selectables['spase://IMPEX/Repository/FMI/HYB']).toEqual(['NumericalOutput' ]);
		expect(rService.selectables['spase://IMPEX/Repository/FMI/GUMICS']).toEqual(['NumericalOutput']);
		expect(rService.selectables['spase://IMPEX/Repository/LATMOS']).toEqual(['SimulationRun','NumericalOutput']);
		expect(rService.selectables['spase://IMPEX/Repository/SINP']).toEqual(['SimulationModel','NumericalOutput']);
	});

	it('should fetch repository', function(){
		var promise = rService.Repository().get({ fmt: 'json' , id: 'spase://IMPEX/Repository/FMI/HYB' }).$promise;
		$httpBackend.flush();
		promise.then(function(r){
			expect(r.resources).toEqual(repos.resources)
		});
	});
	
	// @TODO test the rest of resources (maybe not needed)
	
	it('notify system on selection save', function(){
		var id = 'spase://IMPEX/Repository/FMI/HYB';
		rService.notify('success', id);
		expect(scope.$broadcast).toHaveBeenCalledWith('database-success', id);
	});
	
	
});